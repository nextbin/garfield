package com.latios.garfield.core.util;

import com.latios.garfield.core.util.Md5Util;
import org.junit.Test;

/**
 * @author zebin
 * @since 2016-10-05.
 */
public class Md5UtilTest {
    @Test
    public void md5() {
        System.out.println(Md5Util.md5("111"));
//        System.out.println(Md5Util.md5(null));
    }

}