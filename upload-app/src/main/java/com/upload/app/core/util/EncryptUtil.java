
package com.upload.app.core.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;
import java.util.TreeMap;

public class EncryptUtil {

	private static String getDigest(TreeMap<String, String> map){
		StringBuilder sb = new StringBuilder();
		for (Map.Entry entry : map.entrySet()) {
			if(!entry.getKey().equals("signature ")) {
				sb = sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}
		}
		return sb.toString().substring(0,sb.length()-1);
	}



	/**
	 * 将加密后的字节数组转换成字符串
	 *
	 * @param b 字节数组
	 * @return 字符串
	 */
	private static String byteArrayToHexString(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b!=null && n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toLowerCase();
	}
	/**
	 * sha256_HMAC加密
	 * @param map 加密map
	 * @param secret  秘钥
	 * @return 加密后字符串
	 */
	public static String sha256_HMAC(TreeMap<String, String> map,String url, String secret) {
		String hash = "";

		String message ="POST|"+url+"|"+ getDigest(map);
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
			hash = byteArrayToHexString(bytes);

		} catch (Exception e) {
		}
		return hash;
	}

	public static String hmacSign(String url, String secret) {

		return "";
	}



}
