package com.upload.app.modular.system.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public interface TokenDecodeService {

    Map<String, Object> decodeToken(String txid, JSONArray vins, JSONArray vouts, JSONObject vout, String content, JSONObject scriptPubKey, Integer n, StringBuffer scrpit, List<String> addressList);

}
