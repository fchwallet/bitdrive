package com.upload.app.core.util;

import com.alibaba.fastjson.JSON;

public class SmsSendUtil {

    public static final String charset = "utf-8";

	// 用户平台API账号(非登录账号,示例:N1234567)
	public static String account = "N7522121";

    // 用户平台API密码(非登录密码)
	public static String password = "caiyile86118324";


	public static String send(String phone, String msg) {

		  String smsSingleRequestServerUrl = "https://smssh1.253.com/msg/send/json";

		  String MSG = "【MagicPool】你好,你的验证码是"+msg;

		  String report= "true";

	      SmsSendRequest smsSingleRequest = new SmsSendRequest(account, password, MSG, phone,report);

	      String requestJson = JSON.toJSONString(smsSingleRequest);

	      System.out.println("before request string is: " + requestJson);

	      String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);

	      System.out.println("response after request result is :" + response);

	      SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);

	      System.out.println("response  toString is :" + smsSingleResponse);

		return "";

	}

	public static void main(String[] args) {

		send("15967425457","451235");
	}

}
