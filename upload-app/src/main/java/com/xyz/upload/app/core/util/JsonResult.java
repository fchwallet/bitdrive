package com.xyz.upload.app.core.util;

import com.alibaba.fastjson.JSONObject;
import com.xyz.upload.app.core.common.exception.BizExceptionEnum;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author wuchenghua
 */
public class JsonResult {

    /** 错误码 */
    private int code = 200;

    /**
     * 消息
     */
    private String msg = "";

    /**
     * 数据
     */
    private Map<String, Object> data;


    public JsonResult() {
    }

    public JsonResult(int code) {
        this.code = code;
    }

    public JsonResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public JsonResult(BizExceptionEnum statusCode){
        this.code = statusCode.getCode();
        this.msg = statusCode.getMessage();
    }


    /**
     * 返回json对象
     *
     * @return
     */
    public String toJson() {
        return JSONObject.toJSONString(this);
    }

    /**
     * 直接写入客户端
     *
     * @param response
     * @throws IOException
     */
    public void toJson(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(toJson());
    }

    private void initData() {
        if (data == null) {
            data = new HashMap<>();
        }
    }



    /**
     * 加入数据项
     *
     * @param name
     * @param value
     */
    public JsonResult addData(String name, Object value) {
        initData();
        data.put(name, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public JsonResult addData(Object value) {
        if (value != null) {
            initData();
            data.putAll(JSONObject.parseObject((JSONObject.toJSONString(value)), Map.class));
        }
        return this;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
