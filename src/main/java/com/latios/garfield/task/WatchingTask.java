package com.latios.garfield.task;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.latios.garfield.GarfieldConfig;
import com.latios.garfield.GarfieldConsts;
import com.latios.garfield.cli.WatchingCli;
import com.latios.garfield.core.email.EmailSender;
import com.latios.garfield.core.http.Http;
import com.latios.garfield.core.util.Md5Util;
import com.latios.garfield.entity.Email;
import com.latios.garfield.entity.WatchingConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zebin
 * @since 2016-10-05.
 */
public class WatchingTask implements Runnable {

    private static final GarfieldConfig config = GarfieldConfig.getInstance();
    private static final String sendTo = config.get(GarfieldConsts.CONFIG_EMAIL_SEND_TO);
    private static final Gson gson = new Gson();
    private static final Logger LOG = Logger.getLogger(WatchingCli.class);

    @Override
    public void run() {
        try {
            LOG.info("monitor list file: " + GarfieldConsts.FILE_NAME_WATCHING_PAGES_YML);
            LOG.info("link file: " + GarfieldConsts.FILE_NAME_HISTORY_URL_LOG);
            List<String> oldLinks = getHistoryLinks();
            InputStream input = new FileInputStream(GarfieldConsts.FILE_NAME_WATCHING_PAGES_YML);
            Object object = new Yaml().load(input);
            input.close();
            LOG.info("configs: " + object);
            List<WatchingConfig> configs = gson.fromJson(gson.toJson(object), new TypeToken<List<WatchingConfig>>() {
            }.getType());
            Map<WatchingConfig, List<String>> newPage = new HashMap<>();
            List<String> newLinks = new LinkedList<>();
            for (WatchingConfig config : configs) {
                List<String> tmpLinks = getNewLinks(config, oldLinks);
                LOG.info(String.format("isNew=%s, page config=%s", !tmpLinks.isEmpty(), config));
                if (tmpLinks.size() > 0) {
                    newLinks.addAll(tmpLinks);
                    newPage.put(config, tmpLinks);
                }
            }
            LOG.info(String.format("new page size: %s, new page: %s", newPage.size(), newPage));
            if (!newPage.isEmpty()) {
                // send email
                Email email = getEmail(newPage);
                LOG.info("sending email...");
                EmailSender emailSender = new EmailSender();
                emailSender.send(email.getTitle(), email.getContent(), email.getSendTo());
                LOG.info("email send success.");
                // append md5
                appendMd5(newPage.keySet());
                // update link
                OutputStream output = new FileOutputStream(GarfieldConsts.FILE_NAME_HISTORY_URL_LOG, true);
                for (String newLink : newLinks) {
                    output.write((newLink + "\n").getBytes());
                }
                output.flush();
                output.close();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void appendMd5(Collection<WatchingConfig> watchingConfigs) throws IOException {
        List<String> md5List = new LinkedList<>();
        for (WatchingConfig config : watchingConfigs) {
            String url = config.getUrl();
            String selector = config.getSelector();
            Http http = new Http();
            try {
                String html = http.get(url);
                Document doc = Jsoup.parse(html);
                Elements elements = doc.select(selector);
                String md5 = Md5Util.md5(elements.html());
                md5List.add(md5);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        OutputStream outputStream = new FileOutputStream(GarfieldConsts.FILE_NAME_HISTORY_MD5_LOG, true);
        outputStream.write((StringUtils.join(md5List, "\n") + "\n").getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

    private static Email getEmail(Map<WatchingConfig, List<String>> newPage) {
        List<Map<String, String>> rstList = new LinkedList<>();
        String[] columns = new String[]{"剧名", "网页", "剧集名称", "判定区域"};
        for (Map.Entry<WatchingConfig, List<String>> page : newPage.entrySet()) {
            Map<String, String> row = new HashMap<>();
            WatchingConfig config = page.getKey();
            row.put(columns[0], config.getName());
            row.put(columns[1], config.getUrl());
            row.put(columns[3], config.getSelector());
            for (String link : page.getValue()) {
                Map<String, String> tmp = new HashMap<>(row);
                tmp.put(columns[2], link);
                rstList.add(tmp);
            }
        }
        // 同个剧名的只保留第一行的名称,其他设为null
        Set<String> names = new HashSet<>();
        for (Map<String, String> row : rstList) {
            if (names.contains(row.get(columns[0]))) {
                row.remove(columns[0]);
            }
            names.add(row.get(columns[0]));
        }
        String dateStr = new SimpleDateFormat("[yyyy-MM-dd HH:mm] ").format(new Date());
        Email email = new Email(dateStr + "[Garfield] 剧集更新提醒", sendTo);
        email.addTable("剧集更新提醒", rstList, Arrays.asList(columns));
        return email;
    }

    private static List<String> getHistoryLinks() throws IOException {
        File file = new File(GarfieldConsts.FILE_NAME_HISTORY_URL_LOG);
        if (!file.exists()) {
            if (file.createNewFile()) {
                LOG.info(String.format("create %s success", GarfieldConsts.FILE_NAME_HISTORY_URL_LOG));
            } else {
                LOG.error(String.format("create %s fail, exit now!", GarfieldConsts.FILE_NAME_HISTORY_URL_LOG));
                System.exit(1);
            }
        }
        return FileUtils.readLines(file, "UTF-8");
    }

    private static List<String> getNewLinks(WatchingConfig config, List<String> oldLinks) throws Exception {
        List<String> newLinks = new LinkedList<>();
        String url = config.getUrl();
        String selector = config.getSelector();
        Http http = new Http();
        String html = http.get(url);
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(selector);
        String currentMd5 = Md5Util.md5(elements.html());
        boolean isNew = !existMd5(currentMd5);
        if (isNew) {
            for (Element element : elements) {
                String linkHtml = element.outerHtml();
                if (!oldLinks.contains(linkHtml)) {
                    newLinks.add(linkHtml);
                }
            }
        }
        return newLinks;
    }

    private static boolean existMd5(String md5) throws IOException {
        File file = new File(GarfieldConsts.FILE_NAME_HISTORY_MD5_LOG);
        if (!file.exists()) {
            if (file.createNewFile()) {
                LOG.info(String.format("create %s success", GarfieldConsts.FILE_NAME_HISTORY_MD5_LOG));
            } else {
                LOG.error(String.format("create %s fail, exit now!", GarfieldConsts.FILE_NAME_HISTORY_MD5_LOG));
                System.exit(1);
            }
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals(md5)) {
                return true;
            }
        }
        return false;
    }

}
