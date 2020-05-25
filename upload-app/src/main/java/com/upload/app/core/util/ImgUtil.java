package com.upload.app.core.util;

import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImgUtil {

    public static InputStream aa(String imgBase64 ,String s)throws IOException {
        String str = imgBase64.replace("data:image/"+s+";base64,", "");//去除base64中无用的部分

        BASE64Decoder decoder = new BASE64Decoder();

        // 解密
        byte[] b = decoder.decodeBuffer(str);
        // 处理数据
        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {
                b[i] += 256;
            }
        }
        InputStream i = byte2Input(b);

        return i;
    }

    public static InputStream byte2Input(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }
}
