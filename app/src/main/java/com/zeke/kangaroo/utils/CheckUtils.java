package com.zeke.kangaroo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * date 16/10/4 下午4:15
 * description 身份证号码，邮箱，电话号码 的严格验证
 */
public class CheckUtils {
    //    位权值数组
    private static byte[] Wi=new byte[17];
    //    身份证前部分字符数
    private static final byte fPart = 6;
    //    身份证算法求模关键值
    private static final byte fMod = 11;
    //    旧身份证长度
    private static final byte oldIDLen = 15;
    //    新身份证长度
    private static final byte newIDLen = 18;
    //    新身份证年份标志
    private static final String yearFlag = "19";
    //    校验码串
    private static final String CheckCode="10X98765432";
    //    最小的行政区划码
    private static final int minCode = 150000;
    //    最大的行政区划码
    private static final int maxCode = 700000;

    private static void setWiBuffer(){
        for(int i=0;i<Wi.length;i++){
            int k = (int) Math.pow(2, (Wi.length-i));
            Wi[i] = (byte)(k % fMod);
        }
    }

    //获取新身份证的最后一位:检验位
    private static String getCheckFlag(String idCard){
        int sum = 0;
        //进行加权求和
        for(int i=0; i<17; i++){
            sum += Integer.parseInt(idCard.substring(i,i+1)) * Wi[i];
        }
        //取模运算，得到模值
        byte iCode = (byte) (sum % fMod);
        return CheckCode.substring(iCode,iCode+1);
    }

    //判断串长度的合法性
    private static boolean checkLength(final String idCard){
        return (idCard.length() == oldIDLen) || (idCard.length() == newIDLen);
    }

    //获取时间串
    private static String getIDDate(final String idCard, boolean newIDFlag){
        String dateStr ;
        if(newIDFlag) {
            dateStr = idCard.substring(fPart, fPart + 8);
        } else {
            dateStr = yearFlag + idCard.substring(fPart, fPart + 6);
        }
        return dateStr;
    }

    //判断时间合法性
    private static boolean checkDate(final String dateSource){
        String dateStr = dateSource.substring(0,4)+"-"+dateSource.substring(4,6)+"-"+dateSource.substring(6,8);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date= formatter.parse(dateStr);
            return (date!=null);
        } catch (ParseException e) {
            return false;
        }
    }

    //旧身份证转换成新身份证号码
    public static String getNewIDCard(final String oldIDCard){
        //初始化方法
        CheckUtils.setWiBuffer();
        String newIDCard = oldIDCard.substring(0, fPart);
        newIDCard += yearFlag;
        newIDCard += oldIDCard.substring(fPart, oldIDCard.length());
        String ch = getCheckFlag(newIDCard);
        newIDCard += ch;
        return newIDCard;
    }

    //新身份证转换成旧身份证号码
    public static String getOldIDCard(final String newIDCard){
        //初始化方法
        CheckUtils.setWiBuffer();
        if(!checkIDCard(newIDCard)){
            return newIDCard;
        }
        return newIDCard.substring(0, fPart)+
                newIDCard.substring(fPart+yearFlag.length(),newIDCard.length()-1);
    }

    //判断身份证号码的合法性
    public static boolean checkIDCard( String idCard){

        //初始化方法
        CheckUtils.setWiBuffer();

        if(idCard == null || idCard.length()< 15 || idCard.length() > 18)
            return false;

        boolean isNew = idCard.length() == 18;
        if(!isNew){
            idCard = getNewIDCard(idCard);
        }
        String idDate = getIDDate(idCard, true);
        if(!checkDate(idDate)){
            return false;
        }

        String checkFlag = getCheckFlag(idCard);
        String theFlag = idCard.substring(idCard.length() - 1, idCard.length());
        return checkFlag.equals(theFlag);
    }

    /**
     * 验证邮箱
     * @param email 邮箱
     * @return 是否合法
     */
    public static boolean checkEmail(String email){
        boolean flag;
        try{
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }

    /**
     * 验证手机号码
     * @param mobiles 手机号码
     * @return 是否合法
     */
    public static boolean checkMobileNO(String mobiles){

        Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
     }


    /**
     * 密码- 6到26位的字母和数字组合
     * @param password 密码
     * @return 是否合法
     */
    public static boolean checkPassword(String password){
        Pattern p = Pattern.compile("^[0-9a-zA-Z]{6,20}$");
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public CheckUtils(){
        setWiBuffer();
    }

}


