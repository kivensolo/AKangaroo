package com.zeke.kangaroo.utils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * author：KingZ
 * date：2019/12/12
 * description： Uri tools.
 */
public class UriUtils {
    private UriUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    /**
     * 检查URI地址是否是有效的
     *
     * @return true 有效
     * @param uri 要校验的URI
     */
    public static boolean isValid(URI uri){
        return !(uri.getHost() == null || uri.getPath() == null);
    }
}
