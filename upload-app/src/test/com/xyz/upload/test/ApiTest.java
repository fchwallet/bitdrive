package com.xyz.upload.test;
//
//import com.alibaba.fastjson.JSONObject;
//import com.xyz.upload.app.core.util.EncryptUtil;
//import com.xyz.upload.app.core.util.HttpUtil;
//import org.junit.Test;
//
//import java.util.Date;
//import java.util.TreeMap;
//
//public class ApiTest extends ApplicationTest {
//
//    @Test
//    public void getBalanceTest() {
//
//        TreeMap<String, String> queryParas = new TreeMap<>();
//        queryParas.put("access_key", "你的access_key");
//        queryParas.put("uid", "你的uid");
//        queryParas.put("tnonce", new Date().getTime()+"");
//
//        String sign = EncryptUtil.sha256_HMAC(queryParas, "/api/getBalance", "你的key");
//        queryParas.put("signature",sign);
//
//        String reulst = HttpUtil.doPost("http://localhost:8422/api/getBalance",queryParas);
//
//        JSONObject json = (JSONObject) JSONObject.parse(reulst);
//        System.out.println(json);
//
//    }
//
//    @Test
//    public void updateBalanceTest() {
//
//        TreeMap<String, String> queryParas = new TreeMap<>();
//        queryParas.put("access_key", "1c47c3be44a56a04e471fa699bd864e2");
//        queryParas.put("uid", "123456");
//        queryParas.put("change", "1");
//        queryParas.put("tnonce", new Date().getTime()+"");
//
//        String sign = EncryptUtil.sha256_HMAC(queryParas, "/api/updateBalance", "f1a4fb32f9150e023d40bb09c5c7eda4");
//        queryParas.put("signature",sign);
//        queryParas.put("code","123123123");
//
//        String reulst = HttpUtil.doPost("http://localhost:8422/api/updateBalance",queryParas);
//
//        JSONObject json = (JSONObject) JSONObject.parse(reulst);
//        System.out.println(json);
//
//    }
//
//    @Test
//    public void addUserTest() {
//
//        TreeMap<String, String> queryParas = new TreeMap<>();
//        queryParas.put("access_key", "你的access_key");
//        queryParas.put("uid", "你的uid");
//        queryParas.put("tnonce", new Date().getTime()+"");
//
//        String sign = EncryptUtil.sha256_HMAC(queryParas, "/api/addUser", "你的key");
//        queryParas.put("signature",sign);
//
//        String reulst = HttpUtil.doPost("http://localhost:8422/api/addUser",queryParas);
//
//        JSONObject json = (JSONObject) JSONObject.parse(reulst);
//        System.out.println(json);
//
//    }
//
//    @Test
//    public void getUserAllBalanceTest() {
//
//        TreeMap<String, String> queryParas = new TreeMap<>();
//        queryParas.put("access_key", "1c47c3be44a56a04e471fa699bd864e2");
//        queryParas.put("tnonce", new Date().getTime()+"");
//
//        String sign = EncryptUtil.sha256_HMAC(queryParas, "/api/getUserAllBalance", "f1a4fb32f9150e023d40bb09c5c7eda4");
//        queryParas.put("signature",sign);
//        queryParas.put("page","1");
//
//        String reulst = HttpUtil.doPost("http://116.62.126.223:8422/api/getUserAllBalance",queryParas);
//
//        JSONObject json = (JSONObject) JSONObject.parse(reulst);
//        System.out.println(json);
//
//    }
//
//    @Test
//    public void getBalanceHistory() {
//
//        TreeMap<String, String> queryParas = new TreeMap<>();
//        queryParas.put("access_key", "1c47c3be44a56a04e471fa699bd864e2");
//        queryParas.put("uid","123456");
//        queryParas.put("tnonce", new Date().getTime()+"");
//
//        String sign = EncryptUtil.sha256_HMAC(queryParas, "/api/getBalanceHistory", "f1a4fb32f9150e023d40bb09c5c7eda4");
//        queryParas.put("signature",sign);
//        queryParas.put("page","1");
//
//        String reulst = HttpUtil.doPost("http://localhost:8422/api/getBalanceHistory",queryParas);
//
//        JSONObject json = (JSONObject) JSONObject.parse(reulst);
//        System.out.println(json);
//
//    }
//
//}

import com.alibaba.fastjson.JSONObject;
import com.xyz.upload.app.core.util.EncryptUtil;
import com.xyz.upload.app.core.util.HttpUtil;

import java.util.Date;
import java.util.TreeMap;

public class ApiTest {

    public static void main(String[] args) throws Exception {
//        TreeMap<String, String> queryParas = new TreeMap<>();
//        queryParas.put("access_key", "1c47c3be44a56a04e471fa699bd864e2");
//        queryParas.put("uid", "1247491751887282178");
//        queryParas.put("code", "888800100000000235");
//        queryParas.put("change", "-10");
//        String tnonce = System.currentTimeMillis() + "";
//        queryParas.put("tnonce", tnonce);
////
////
//        String sign = EncryptUtil.sha256_HMAC(queryParas, "/api/updateBalance", "f1a4fb32f9150e023d40bb09c5c7eda4");
//        System.out.println(sign);
//        System.out.println(tnonce);



        TreeMap<String, String> queryParas = new TreeMap<>();
        queryParas.put("access_key", "1c47c3be44a56b234fa699bd864e2");
        queryParas.put("tnonce", new Date().getTime()+"");

        String sign = EncryptUtil.sha256_HMAC(queryParas, "/api/download", "f1a4fb32f9150e0dffebd09c5c7eda4");
        queryParas.put("signature",sign);
        System.out.println(json);
//        String reulst = HttpUtil.doPost("http://116.62.126.223:8422/api/getBalance",queryParas);

        JSONObject json = (JSONObject) JSONObject.parse(reulst);
        System.out.println(json);


//        queryParas.put("signature", sign);
//        queryParas.put("page", "1");

//        TreeMap<String, String> queryParas = new TreeMap<>();
////        queryParas.put("access_key", "1c47c3be44a56a04e471fa699bd864e2");
////        queryParas.put("uid", "2342342342342345");
////        queryParas.put("tnonce", new Date().getTime()+"");
////        String tnonce = System.currentTimeMillis() + "";
////
////        String sign = EncryptUtil.sha256_HMAC(queryParas, "/api/addUser", "f1a4fb32f9150e023d40bb09c5c7eda4");
////        queryParas.put("signature",sign);
////
//        System.out.println(sign);
//        System.out.println(tnonce);


    }

}
