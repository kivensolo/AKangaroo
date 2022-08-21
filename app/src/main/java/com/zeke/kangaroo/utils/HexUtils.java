package com.zeke.kangaroo.utils;

import android.text.TextUtils;

/**
 * author：ZekeWang
 * date：2022/8/21
 * description：Transform utils for Byte、Char、String.
 */
public class HexUtils {

    //private static final char[] HEX_CHAR_ARRAY = "0123456789abcdef".toCharArray();

    private static final char[] HEX_LOWER = new char[]{
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
    };

    private static final char[] HEX_UPPER = new char[]{
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F'
    };

    // <editor-fold defaultstate="collapsed" desc="bytes2HexString">
    /**
     * Convert-from-byte-array-to-hex-string
     * Taken from:
     * http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java/9655275#9655275
     * @param bytes byte-array
     * @param hexChars
     * @return HexString
     */
    public static String bytesToHex(byte[] bytes, char[] hexChars) {
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_LOWER[v >>> 4];
            hexChars[j * 2 + 1] = HEX_LOWER[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 转换成十六进制字符串
     * 通过Integer转换，速度慢
     * @param bts bytes
     * @return hex string
     */
    private static String byte2Hex(byte[] bts) {
        StringBuilder des = new StringBuilder();
        String tmp = null;

        for (byte bt : bts) {
            tmp = (Integer.toHexString(bt & 0xFF));
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }

    public static String byte2HexExt(byte[] bytes) {
        return byte2HexExt(bytes,true, false);
    }

    /**
     * Bytes to hexString use string format;
     * @param bytes     string bytes
     * @param upperCase output upperCase
     * @param appendSpace addSpace
     * @return HexString
     */
    public static String byte2HexExt(byte[] bytes,boolean upperCase, boolean appendSpace) {
        StringBuilder stmp = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = String.format("%02X", bytes[i]);
            String hexCase = upperCase ? hex.toUpperCase() : hex.toLowerCase();
            stmp.append(hexCase);
            if (appendSpace && i != (bytes.length - 1)) {
                stmp.append(" ");
            }
        }
        return stmp.toString();
    }

    public static String byte2HexFast(byte[] bytes){
        return byte2HexFast(bytes,false,false);
    }

    public static String byte2HexFast(byte[] bytes,boolean upperCase, boolean appendSpace) {
        StringBuilder sb = new StringBuilder();
        int num;
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i];
            num = b < 0 ? 256 + b : b;
            char bit_h;
            char bit_l;
            if(upperCase){
                bit_h = HEX_UPPER[num / 16];
                bit_l = HEX_UPPER[num % 16];
            }else{
                bit_h = HEX_LOWER[num / 16];
                bit_l = HEX_LOWER[num % 16];
            }
            sb.append(bit_h).append(bit_l);
            if(appendSpace && i !=(bytes.length - 1)){
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    // </editor-fold>

// <editor-fold defaultstate="collapsed" desc="HexString2Bytes">
    /**
     * Hex string to bytes array.
     * @param hexstr String of hex
     * @return Bytes value in String.
     */
    public static byte[] hexString2Bytes(String hexstr) {
        if (hexstr == null || TextUtils.equals(hexstr.trim(), "")) {
            return new byte[0];
        }
        int bytesLen = hexstr.length() / 2;
        byte[] b = new byte[bytesLen];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((char2Int(c0) << 4) | char2Int(c1));
        }
        //String hex;
        //for (int i = 0; i < bytesLen; i++) {
        //	hex = hexString.substring(i * 2, i * 2 + 2);
        //	bytes[i] = (byte) Integer.parseInt(hex, 16);
        //}
        return b;
    }
// </editor-fold>

    /**
     * Char to ASCII Value
     * @param c char
     * @return ASCII Value
     * '0' = 32
     * 'a' = 97
     * 'A' = 65
     */
    public static int char2Int(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }
}
