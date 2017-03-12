package com.latios.garfield.task;

import com.google.gson.Gson;
import com.latios.garfield.GarfieldConfig;
import com.latios.garfield.GarfieldConsts;
import com.latios.garfield.core.email.EmailSender;
import com.latios.garfield.core.util.Md5Util;
import com.latios.garfield.entity.Email;
import com.latios.garfield.entity.WatchingConfig;
import com.latios.garfield.core.http.Http;
import com.latios.garfield.cli.WatchingCli;
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
            LOG.info("monitor list file: " + GarfieldConsts.FILE_NAME_PAGES_YML);
            LOG.info("link file: " + GarfieldConsts.FILE_NAME_LINK_LOG);
            List<String> oldLinks = getOldLinks();
            InputStream input = new FileInputStream(GarfieldConsts.FILE_NAME_PAGES_YML);
            Object object = new Yaml().load(input);
            input.close();
            LOG.info("configs: " + object);
            List<WatchingConfig> configs = (List<WatchingConfig>) object;
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
                // update config
                String updateStr = new Yaml().dump(configs);
                OutputStream output = new FileOutputStream(GarfieldConsts.FILE_NAME_PAGES_YML);
                output.write(updateStr.getBytes());
                output.flush();
                output.close();
            }
            // update link
            if (!newLinks.isEmpty()) {
                OutputStream output = new FileOutputStream(GarfieldConsts.FILE_NAME_LINK_LOG, true);
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

    private static List<String> getOldLinks() throws IOException {
        List<String> ret = new LinkedList<>();
        File file = new File(GarfieldConsts.FILE_NAME_LINK_LOG);
        if (!file.exists()) {
            String res = file.createNewFile() ? "success" : "fail";
            LOG.info(String.format("create file %s %s", GarfieldConsts.FILE_NAME_LINK_LOG, res));
        }
        BufferedReader input = new BufferedReader(new FileReader(GarfieldConsts.FILE_NAME_LINK_LOG));
        String str;
        while ((str = input.readLine()) != null) {
            ret.add(str);
        }
        input.close();
        return ret;
    }

    public static List<String> getNewLinks(WatchingConfig config, List<String> oldLinks) throws Exception {
        List<String> newLinks = new LinkedList<>();
        String url = config.getUrl();
        String selector = config.getSelector();
        Http http = new Http();
        String html = http.get(url);
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(selector);
        String currentMd5 = Md5Util.md5(elements.html());
        boolean isNew = !config.getSupposeMd5().equals(currentMd5);
        config.setSupposeMd5(currentMd5);
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

}
