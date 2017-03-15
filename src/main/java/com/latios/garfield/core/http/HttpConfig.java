package com.latios.garfield.core.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zebin
 * @since 2016-10-04.
 */
public class HttpConfig {

    private static final String HEADER_FILED_USER_AGENT = "User-Agent";
    private static final String HEADER_VALUE_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";

    public HttpConfig() {
        header = new HashMap<>();
        header.put(HEADER_FILED_USER_AGENT, HEADER_VALUE_USER_AGENT);
    }

    private long retryInterval = 1000 * 2;
    private long timeout = 1000 * 60;
    private Map<String, String> header = null;

    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }
}
