package com.xyz.upload.app.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.security.MessageDigest.getInstance;

public class UnicodeUtil {

    public static String getSHA256(String str){
         MessageDigest messageDigest;
        String encodestr = "";
         try {
        messageDigest = getInstance("SHA-256");
        messageDigest.update(str.getBytes("UTF-8"));
             encodestr  = byte2Hex(messageDigest.digest());
         } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
        }
        return encodestr;
        }

        private static String byte2Hex(byte[] bytes){
 StringBuffer stringBuffer = new StringBuffer();
String temp = null;
 for (int i=0;i<bytes.length;i++){
temp = Integer.toHexString(bytes[i] & 0xFF);
if (temp.length()==1){
//1得到一位的进行补0操作
stringBuffer.append("0");
}
stringBuffer.append(temp);
 }
 return stringBuffer.toString();
}

    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }

    public static void byteTofile(byte[] data,String path){
        if(data.length<3||path.equals("")) return;
        try{
            FileOutputStream imageOutput = new FileOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            System.out.println("Make Picture success,Please find image in " + path);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }


    public static String str2HexStr(String str) {
        char[] chars = "0123456789abcdef".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }

     //10进制转16进制
    public static String intToHex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        while(n != 0){
            s = s.append(b[n%16]);
            n = n/16;
        }
        a = s.reverse().toString();
        HexstrAddZero(a);
        return HexstrAddZero(a);
    }

    public static String StringToHex(String n) {
        return HexstrAddZero(n);
    }


//    public static String intToHex(int n) {
//        String tmp = "0X" + StringUtils.leftPad(Integer.toHexString(n).toUpperCase(), 2, '0');
//        return tmp;
//    }


    //将16进制字符串低位字节补零
    //string str= Convert.ToString(a, 16);
    //str为整数a对应的十六进制字符串
    public static String HexstrAddZero(String str)
    {
        String strByeZero = "";
        if(str.length() >= 2)
        {
            strByeZero = str;
        }
        else if(str.length() == 1)
        {
            strByeZero = "0"+str;
        }
        else if(str.length() == 0)
        {
            strByeZero = "00";
        }
        return strByeZero;
    }

    public static int decodeHEX(String hexs){
        BigInteger bigint=new BigInteger(hexs, 16);
        int numb=bigint.intValue();
        return numb;
    }

}
