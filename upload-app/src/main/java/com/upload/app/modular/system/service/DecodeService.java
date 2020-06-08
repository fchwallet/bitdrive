package com.upload.app.modular.system.service;

import com.alibaba.fastjson.JSONArray;

import java.math.BigDecimal;

public interface DecodeService {

     String decodeCreate(JSONArray jsonArray, BigDecimal sumFee, String driveId) throws Exception;

     void blockDecode(JSONArray jsonArray) throws Exception;

}
