package com.xyz.upload.app.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

public class UnicodeUtil {

    public static byte[] SendS(String str){
        byte[] ok = new byte[0];
        try {
            ok = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ok;
    }

    public static char[] chars = "0123456789abcdef".toCharArray();
    public static String toHex(String str) {
        StringBuilder builder = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            builder.append(chars[bit]);
            bit = bs[i] & 0x0f;
            builder.append(chars[bit]);
        }
        return builder.toString().trim();
    }


    public static String bytesToHexString(byte[] src){

        StringBuilder stringBuilder = new StringBuilder("");

        if (src == null || src.length <= 0) {
            return null;
        }

        for (int i = 0; i < src.length; i++) {

            int v = src[i] & 0xFF;

            String hv = Integer.toHexString(v);

            if (hv.length() < 2) {
                stringBuilder.append(0);
            }

            stringBuilder.append(hv);
        }

        return stringBuilder.toString();

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
        return (byte) "0123456789ABCDEF".indexOf(c);
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

}
