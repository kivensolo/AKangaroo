package com.zeke.kangaroo.utils.pattner;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author：ZekeWang
 * date：2021/6/18
 * description：正则表达式工具类
 */
public class PattnerUtils {

    // <editor-fold defaultstate="collapsed" desc="图片相关的正则">

    /**
     * 获取图片前缀路径
     */
    public static String parsePicPrefix(@NonNull String picUrl){
        Matcher picMatcher = getPicMatcher(picUrl);
        if(picMatcher == null){
            return "";
        }else{
            return picMatcher.group(1);
        }
    }

    /**
     * 获取图片格式
     */
    public static String parsePicSuffix(@NonNull String picUrl){
        Matcher picMatcher = getPicMatcher(picUrl);
        if(picMatcher == null){
            return "";
        }else{
            return picMatcher.group(3);
        }
    }

    /**
     * 获取图片名称&后缀格式
     */
    public static String parsePicNameAndSuffix(@NonNull String picUrl){
        Matcher picMatcher = getPicMatcher(picUrl);
        if(picMatcher == null){
            return "";
        }else{
            return picMatcher.group(2);
        }
    }

    /**
     * 获取图片的
     * @param picUrl  图片Url
     * @return Matcher mathcer对象, 如果为NULL，标识图片地址不合法
     *
     * 例如图片url为:
     *  http://116.77.70.230/nn_img/prev/StarCor/epgimg/3/7/3/335f9ad9865b120a74d67a3474ad61c3.png
     *
     *  Matcher.group(0): 图片原始URL
     *  Matcher.group(1): 图片Host&虚拟目录路径(不包含最后一个/)
     *                    如：http://116.77.70.230/nn_img/prev/StarCor/epgimg/3/7/3
     *  Matcher.group(2): 图片名称&后缀格式
     *                    如：335f9ad9865b120a74d67a3474ad61c3.png
     *  Matcher.group(3): 图片格式
     *                    如: png
     */
    private static Matcher getPicMatcher(@NonNull String picUrl) {
         Pattern picCompile = Pattern.compile("^(.+)/+(.*\\.(JPEG|JPG|PNG|GIF|WebP))$",
                                            Pattern.CASE_INSENSITIVE);
        Matcher matcher = picCompile.matcher(picUrl);
        return matcher.matches() ? matcher : null;
    }
    // </editor-fold>
}
