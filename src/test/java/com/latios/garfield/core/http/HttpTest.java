package com.latios.garfield.core.http;

import com.latios.garfield.core.http.Http;
import org.junit.Test;

/**
 * @author zebin
 * @since 2016-10-30.
 */
public class HttpTest {
    @Test
    public void download() throws Exception {
        String url = "http://www.piaohua.com/html/lianxuju/2016/1007/31363.html";
        Http http = new Http();
        System.out.println(http.get(url));
    }

}