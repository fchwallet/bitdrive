package com.upload.app.modular.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.upload.app.modular.system.model.AddressDriveLink;
import com.upload.app.modular.system.model.FchXsvLink;
import com.upload.app.modular.system.service.AddressDriveLinkService;
import com.upload.app.modular.system.service.FchXsvLinkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 接口控制器提供
 *
 * @author stylefeng
 * @Date 2018/7/20 23:39
 */
@RestController
@RequestMapping("/statistics")
@Slf4j
public class StatisticsController {

    @Autowired
    private FchXsvLinkService fchXsvLinkService;

    @Autowired
    private AddressDriveLinkService addressDriveLinkService;

    @ResponseBody
    @RequestMapping(value="/get", method = RequestMethod.POST)
    public JSONObject get() {

        JSONObject ob = new JSONObject();

//        if (StringUtils.isEmpty(name)) {
//            ob.put("code", 400);
//            ob.put("msg", "参数错误");
//            return ob;
//        }

//        if ("fch".equals(name.toLowerCase())) {

        List<FchXsvLink> fchXsvList = fchXsvLinkService.findByType(0);

        ob.put("fchcount", fchXsvList.size());
        Integer i = 0;
        Integer j = 0;

        for (FchXsvLink f : fchXsvList) {

            List<AddressDriveLink> addressDriveList = addressDriveLinkService.findByAddress(f.getAddressHash(), 0);

            if (addressDriveList != null)
                i = addressDriveList.size() + i;

            List<AddressDriveLink> addressUpdateDriveList = addressDriveLinkService.findByAddress(f.getAddressHash(), 1);
            if (addressUpdateDriveList != null)
                j = addressUpdateDriveList.size() + j;

        }

        ob.put("fchCreateCount", i);
        ob.put("fchUpdateCount", j);

        List<FchXsvLink> xsvlink = fchXsvLinkService.findByType(1);
        ob.put("xsvcout", xsvlink.size());


        return ob;

    }


}
