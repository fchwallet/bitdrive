package com.upload.app.modular.api;

import com.alibaba.fastjson.JSONObject;
import com.upload.app.core.rpc.Api;
import com.upload.app.modular.system.model.BalanceHistory;
import com.upload.app.modular.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/points")
@Slf4j
public class PointsController {

    @Autowired
    private SendService sendService;

    @Autowired
    private BalanceHistoryService balanceHistoryService;

    @ResponseBody
    @RequestMapping(value="/charge", method = RequestMethod.POST)
    public JSONObject charge(String address, String value) throws Exception {

        JSONObject ob = new JSONObject();

        if ("F".equals(address) || "f".equals(address)) {
            address = Api.fchtoxsv(address).getString("address");
        } else if ("1".equals(address)) {
            address = address;
        }

        Boolean flag = sendService.Create(address, value);

        if (flag) {
            BalanceHistory balanceHistory = new BalanceHistory();
            balanceHistory.setTimestamp(new Date());
            balanceHistory.setType("recharge");
            balanceHistory.setAddress(address);
            balanceHistory.setChange(Integer.valueOf(value));
            balanceHistoryService.insert(balanceHistory);
            ob.put("code", 200);
            ob.put("msg", "充值成功");
        } else {
            ob.put("code", 100088);
            ob.put("msg", "充值失败");
        }
        return ob;

    }


}
