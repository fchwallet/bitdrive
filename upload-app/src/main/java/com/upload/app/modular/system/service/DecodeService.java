package com.upload.app.modular.system.service;

import com.alibaba.fastjson.JSONArray;

public interface DecodeService {

     String decodeCreate(JSONArray jsonArray, String driveId) throws Exception;

     void blockDecode(JSONArray jsonArray) throws Exception;

}
