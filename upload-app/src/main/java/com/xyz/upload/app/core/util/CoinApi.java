package com.xyz.upload.app.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class CoinApi {

		//撮合
	private static final String url = "http://116.62.126.223:8080";


	/**
	 * 更新用户资产
	 * @param uid
	 * @param change
	 * @return
	 * @throws Exception
	 */
	public static JSONObject updateBalance(Long uid, String change) throws Exception {

		JSONObject json = new JSONObject();
		json.put("id", 1000);
		json.put("method", "balance.update");
		JSONArray jsons = new JSONArray();
		jsons.add(uid);
		jsons.add("SIMI");
		jsons.add("");
		jsons.add(new Date().getTime());
		jsons.add(change);
		jsons.add(new JSONObject());
		json.put("params", jsons);

		JSONObject JSON = HttpResult(json.toString());

		return JSON;

	}

	/**
	 * 查询⽤户资产
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public static JSONObject queryBalance(Long uid) throws Exception {

		JSONObject json = new JSONObject();
		json.put("id", 1000);
		json.put("method", "balance.query");
		JSONArray jsons = new JSONArray();
		jsons.add(uid);
		jsons.add("SIMI");
		json.put("params", jsons);

		JSONObject JSON = HttpResult(json.toString());

		return JSON;

	}

	/**
	 * 查询用户资产历史
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public static JSONObject balanceHistory(Long uid, Integer offset, Integer limit) throws Exception {

		JSONObject json = new JSONObject();
		json.put("id", 1000);
		json.put("method", "balance.history");
		JSONArray jsons = new JSONArray();
		jsons.add(uid);
		jsons.add("SIMI");
		jsons.add("");
		jsons.add(0);
		jsons.add(0);
		jsons.add(offset);
		jsons.add(limit);
		json.put("params", jsons);

		JSONObject JSON = HttpResult(json.toString());

		return JSON;

	}


	public static JSONObject HttpResult(String json) throws Exception {

		String jsonresult = HttpUtil.doPost(url, json);

		JSONObject JSON = (JSONObject) JSONObject.parse(jsonresult);

		return JSON;

	}

}
