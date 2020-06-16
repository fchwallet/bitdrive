package com.upload.app.modular.api;

import com.alibaba.fastjson.JSONObject;
import com.upload.app.core.rpc.Api;
import com.upload.app.modular.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/points")
@Slf4j
public class PointsController {

    @Resource
    private SendService sendService;

    @ResponseBody
    @RequestMapping(value="/recharge", method = RequestMethod.POST)
    public JSONObject recharge(String address, String value) throws Exception {

        JSONObject ob = new JSONObject();

        if ("F".equals(address) || "f".equals(address)) {
            address = Api.fchtoxsv(address).getString("address");
        } else if ("1".equals(address)) {
            address = address;
        }

        Boolean flag = sendService.Create(address, value);

        if (flag) {
            ob.put("code", 200);
            ob.put("msg", "充值成功");
        } else {
            ob.put("code", 100088);
            ob.put("msg", "充值失败");
        }
        return ob;

    }


}
