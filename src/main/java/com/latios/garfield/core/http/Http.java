package com.latios.garfield.core.http;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * @author zebin
 * @since 2016-10-04.
 */
public class Http {
    private HttpConfig config = new HttpConfig();

    private static final Logger LOG = Logger.getLogger(Http.class);

    public String get(String url) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        for (Map.Entry<String, String> header : config.getHeader().entrySet()) {
            httpGet.setHeader(header.getKey(), header.getValue());
        }
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout((int) config.getTimeout())
                .setConnectionRequestTimeout((int) config.getTimeout())
                .setSocketTimeout((int) config.getTimeout()).build();
        httpGet.setConfig(requestConfig);
        Exception e = null;
        int retry = 3;
        while (retry-- > 0) {
            try {
                CloseableHttpResponse response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            } catch (ParseException | IOException e1) {
                long sleep = config.getRetryInterval();
                LOG.warn(String.format("http get failed, sleep: %s, left retry: %s, msg: %s", sleep, retry, e1.getMessage()));
                Thread.sleep(sleep);
                e = e1;
            }
        }
        client.close();
        throw e;
    }
}
