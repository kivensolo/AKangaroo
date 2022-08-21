package com.hex;

import com.zeke.kangaroo.utils.HexUtils;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

/**
 */
public class HexUtilsTest {
    @Test
    public void testBytes2HexString() throws UnsupportedEncodingException {
        byte[] bytes = "hello".getBytes("UTF-8");
        //long t1 = System.nanoTime();
        //String byte2HexString = HexUtils.byte2Hex(bytes);
        //System.out.println("byte2HexString by Integer=" + byte2HexString + ";desc=" + (t2 - t1));

        long t2 = System.nanoTime();
        String byte2HexExtLowString = HexUtils.byte2HexExt(bytes,false, false);
        long t3 = System.nanoTime();
        System.out.println("byte2HexExt lowCase=" + byte2HexExtLowString+ ";Desc=" + (t3 - t2));

        String byte2HexExt2 = HexUtils.byte2HexExt(bytes, true, true);
        long t4 = System.nanoTime();
        System.out.println("byte2HexExt upperCase=" + byte2HexExt2 + ";Desc=" + (t4 - t3));

        String byte2HexFast = HexUtils.byte2HexFast(bytes);
        long t5 = System.nanoTime();
        System.out.println("byte2HexFast=" + byte2HexFast + ";Desc=" + (t5- t4));
        assertEquals(byte2HexExtLowString, byte2HexFast);
    }
}
