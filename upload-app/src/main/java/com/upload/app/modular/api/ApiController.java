/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.upload.app.modular.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.upload.app.core.util.HttpUtil;
import com.upload.app.core.util.JsonResult;
import com.upload.app.modular.system.model.*;
import com.upload.app.modular.system.service.*;
import com.upload.app.core.rpc.Api;
import com.upload.app.core.rpc.CommonTxOputDto;
import com.upload.app.core.rpc.TxInputDto;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 接口控制器提供
 *
 * @author stylefeng
 * @Date 2018/7/20 23:39
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController extends BaseController {

    @Autowired
    private AccessService accessService;

    @Autowired
    private AddressDriveLinkService addressDriveLinkService;

    @Value("${system-address}")
    private String systemAddress;

    @Autowired
    private DecodeService decodeService;

    @Autowired
    private CreateService createService;

    @Autowired
    private FchXsvLinkService fchXsvLinkService;

    @Autowired
    private UpdateService updateService;

    @Autowired
    private DriveUtxoService driveUtxoService;

    @Autowired
    private DriveTxAddressService driveTxAddressService;

    @Autowired
    private SystemUtxoService systemUtxoService;

    @Autowired
    private BlockchainPaymentService blockchainPaymentService;

    @Autowired
    private AddressScriptLinkService addressScriptLinkService;

    @Autowired
    private ScriptTokenLinkService scriptTokenLinkService;

    @Autowired
    private ScriptUtxoTokenLinkService scriptUtxoTokenLinkService;

    final Base64.Decoder decoder = Base64.getDecoder();

    final String tokenId = "5ccd3d59da869896140c3175b2a541eec48d3ad2f43eeb273d299c19e7d67e43";

    @ResponseBody
    @RequestMapping(value="/test", method = RequestMethod.POST)
    public JsonResult test(String name, String status) {

        System.out.println(name);
        System.out.println(status);

        return new JsonResult();

    }


    @ResponseBody
    @RequestMapping(value="/put", method = RequestMethod.POST)
    public JSONObject put(@RequestBody String json) throws Exception {

        JSONObject result = new JSONObject();
        JSONObject param = null;
        try {
            param = (JSONObject) JSONObject.parse(json);
        } catch (Exception e) {
            result.put("code",1009);
            result.put("msg","请传正确的json数据");
            return result;
        }

        JSONArray fch_addr = param.getJSONArray("fch_addr");

        String metadata = param.getString("metadata");

        String data = param.getString("data");
        String signautre = param.getString("signature");

        if (fch_addr == null || fch_addr.size() < 0 || StringUtils.isEmpty(metadata) || StringUtils.isEmpty(data) || StringUtils.isEmpty(signautre)) {
            result.put("code", 400);
            result.put("msg", "参数错误");
            return result;
        }

        String ad = fch_addr.getString(0);
        String adfrist = ad.substring(0,1);
        String fchXSVaddress = null;
        if ("F".equals(adfrist) || "f".equals(adfrist)) {
            fchXSVaddress = Api.fchtoxsv(fch_addr.getString(0)).getString("address");
        } else if ("1".equals(adfrist)) {
            fchXSVaddress = ad;
        }


        Boolean b = Api.VerifyMessage(fchXSVaddress, signautre, data);

        if (!b) {
            result.put("code", 1001);
            result.put("msg", "sign验证失败");
            return result;
        }


        Integer size = data.getBytes().length;

        if (size > 1024 * 1024 * 5) {
            result.put("code", 200211);
            result.put("msg", "data不能超过5M");
            return result;
        }

        FchXsvLink fchXsvLink = fchXsvLinkService.findByFch(ad);

        List<String> scriptList = addressScriptLinkService.findListByAddress(fchXsvLink.getAddressHash());

        if (scriptList == null || scriptList.size() < 1) {
            result.put("code", 200212);
            result.put("msg", "用户积分不足");
            return result;
        }

        List<ScriptUtxoTokenLink> scriptUtxoTokenList = scriptUtxoTokenLinkService.findListByScript(scriptList);
        BigInteger balance = new BigInteger("0");

        for (ScriptUtxoTokenLink sut : scriptUtxoTokenList) {
            BigInteger amount = scriptTokenLinkService.selectFAToken(tokenId, sut.getTxid(), sut.getN());
            balance = balance.add(amount);
        }

        if (balance.compareTo(new BigInteger("1000000000")) < 0) {
            result.put("code", 200212);
            result.put("msg", "用户积分不足");
            return result;
        }

        List<String> fchAddress = new ArrayList<>();
        for (Object o : fch_addr) {

            String addr = (String) o;
            FchXsvLink f = fchXsvLinkService.findByFch(addr);

            if (f != null)
                f.getXsvAddress();
            else {
                String xsvaddress = null;
                FchXsvLink insert = new FchXsvLink();
                if ("F".equals(adfrist) || "f".equals(adfrist)) {
                    xsvaddress = Api.fchtoxsv(fch_addr.getString(0)).getString("address");
                    insert.setType(0);
                } else if ("1".equals(adfrist)) {
                    xsvaddress = ad;
                    insert.setType(1);
                }

                insert.setFchAddress(addr);
                insert.setXsvAddress(xsvaddress);


                String addressHash = Api.ValidateAddress(xsvaddress).getString("scriptPubKey").replaceFirst("76a914", "").replaceFirst("88ac", "");
                insert.setAddressHash(addressHash);
                fchXsvLinkService.insert(insert);
                f = fchXsvLinkService.findByFch(addr);
            }

            fchAddress.add(f.getXsvAddress());

        }


        Map<String, Object> map = systemUtxoService.spendUtxo(metadata, fchAddress, size, data, null,1);   //1表示create

        String hex = (String)map.get("hex");
        Object sf = map.get("sumFee");

        JSONArray put = new JSONArray();
        if (!StringUtils.isEmpty(hex) && sf != null) {

            put.add(hex);
            BigDecimal sumFee = (BigDecimal) sf;
            String drive_id = decodeService.decodeCreate(put, sumFee, null);
            result.put("code", 200);
            result.put("drive_id",drive_id);
            blockchainPaymentService.payment(ad,1);         // 查询钱,付费

        } else {

            result.put("code", 500);
            result.put("msg", "处理异常,联系客服");

        }

        return result;

    }


    @ResponseBody
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public JSONObject update(@RequestBody String json) throws Exception {

        JSONObject result = new JSONObject();

        JSONObject param = null;
        try {
            param = (JSONObject) JSONObject.parse(json);
        } catch (Exception e) {
            result.put("code",1009);
            result.put("msg","请传正确的json数据");
            return result;
        }

        JSONArray fch_addr = param.getJSONArray("fch_addr");

        String metadata = param.getString("metadata");

        String data = param.getString("data");

        String signautre = param.getString("signature");

        String drive_id = param.getString("drive_id");


        if (fch_addr == null || fch_addr.size() < 0 || StringUtils.isEmpty(metadata) || StringUtils.isEmpty(data) || StringUtils.isEmpty(signautre) || StringUtils.isEmpty(drive_id)) {
            result.put("code", 400);
            result.put("msg", "参数错误");
            return result;
        }

        String fchadd = fch_addr.getString(0);
        String adfrist = fchadd.substring(0,1);
        String fchXSVaddress = null;
        if ("F".equals(adfrist) || "f".equals(adfrist)) {
            fchXSVaddress = Api.fchtoxsv(fch_addr.getString(0)).getString("address");
        } else if ("1".equals(adfrist)) {
            fchXSVaddress = fchadd;
        }

        Boolean b = Api.VerifyMessage(fchXSVaddress, signautre, data);

        if (!b) {
            result.put("code", 1001);
            result.put("msg", "sign验证失败");
            return result;
        }

        FchXsvLink fxl = fchXsvLinkService.findByFch(fchadd);
        List<AddressDriveLink> ad = addressDriveLinkService.findDriveByAddress(fxl.getAddressHash());
        if (ad.size() <= 0) {
            result.put("code", 1000);
            result.put("msg", "当前操作没有权限");
            return result;
        }

        boolean flag = false;
        for(AddressDriveLink adl : ad) {

            if (adl.getDriveId().equals(drive_id)) {
                flag = true;
                break;
            }

        }

        if (!flag) {
            result.put("code", 1000);
            result.put("msg", "当前操作没有权限");
            return result;
        }


        Integer size = data.getBytes().length;

        if (size > 1024 * 1024 * 15) {
            result.put("code", 200211);
            result.put("msg", "data不能超过5M");
            return result;
        }


        List<String> fchAddress = new ArrayList<>();
        for (Object o : fch_addr) {
            String addr = (String) o;
            FchXsvLink f = fchXsvLinkService.findByFch(addr);
            if (f != null)
                f.getXsvAddress();
            else {
                String xsvaddress = null;
                FchXsvLink insert = new FchXsvLink();
                if ("F".equals(adfrist) || "f".equals(adfrist)) {
                    xsvaddress = Api.fchtoxsv(addr).getString("address");
                    insert.setType(0);
                } else if ("1".equals(adfrist)) {
                    xsvaddress = fchadd;
                    insert.setType(1);
                }
                insert.setFchAddress(addr);
                insert.setXsvAddress(xsvaddress);
                String addressHash = Api.ValidateAddress(xsvaddress).getString("scriptPubKey").replaceFirst("76a914", "").replaceFirst("88ac", "");
                insert.setAddressHash(addressHash);
                fchXsvLinkService.insert(insert);
                f = fchXsvLinkService.findByFch(addr);
            }
            fchAddress.add(f.getXsvAddress());
        }

        DriveUtxo du = driveUtxoService.findByDriveId(drive_id);
        if (du == null) {
            result.put("1002", "该drive_id不存在");
            return result;
        }

        Map<String, Object> map = systemUtxoService.spendUtxo(metadata, fchAddress, size, data, du,2);   //1表示create

        String hex = (String)map.get("hex");
        Object sf = map.get("sumFee");

        JSONArray put = new JSONArray();
        if (!StringUtils.isEmpty(hex) && sf != null) {
            put.add(hex);
            BigDecimal sumFee = (BigDecimal) sf;
            String update_id = decodeService.decodeCreate(put, sumFee, drive_id);
            result.put("code", 200);
            result.put("update_id",update_id);

        } else {

            result.put("code", 500);
            result.put("msg", "处理异常,联系客服");

        }

        return result;

    }


    @ResponseBody
    @RequestMapping(value="/get", method = RequestMethod.POST)
    public JSONObject get(String fch_addr, String drive_id, String update_id) {

        JSONObject json = new JSONObject();

        if (StringUtils.isEmpty(fch_addr) || (StringUtils.isEmpty(drive_id) && StringUtils.isEmpty(update_id))) {
            json.put("code", 400);
            json.put("msg", "参数错误");
            return json;
        }

        FchXsvLink fchXsvLink = fchXsvLinkService.findByFch(fch_addr);

        if (fchXsvLink == null) {
            json.put("code", 1005);
            json.put("msg", "当前地址找不到相应记录，请检查参数");
            return json;
        }


        if (drive_id != null && !drive_id.equals("")) {

            DriveTxAddress driveTxad = driveTxAddressService.findByDrive(fchXsvLink.getAddressHash(), drive_id);

            if (driveTxad != null) {

                Create create = createService.findByDriveId(driveTxad.getDriveId());
                JSONObject ob = new JSONObject();
                ob.put("metadata", create.getMetadata());
                ob.put("data", create.getData());
                json.put("put", ob);

            }

            List<DriveTxAddress> updateTxaddList = driveTxAddressService.findUpdateByDriveList(fchXsvLink.getAddressHash(), drive_id);

            JSONArray obs = new JSONArray();
            for (DriveTxAddress driveTxAddress : updateTxaddList) {

                Update up = updateService.findByDriveId(driveTxAddress.getDriveId(), driveTxAddress.getUpdateId());

                JSONObject ob = new JSONObject();
                ob.put("update_id", up.getUpdateId());
                ob.put("metadata", up.getMetadata());
                ob.put("data", up.getData());
                obs.add(ob);

            }

            json.put("update", obs);

            json.put("code", 200);

        } else if (update_id != null && !update_id.equals("")) {

            DriveTxAddress driveTxAddress = driveTxAddressService.findUpdate(fchXsvLink.getAddressHash(), update_id);

            if (driveTxAddress == null) {
                json.put("update", "找不到更新记录,请检查参数");
                json.put("code", 1006);
                return json;
            }

            Update up = updateService.findByDriveId(driveTxAddress.getDriveId(), driveTxAddress.getUpdateId());

            JSONObject ob = new JSONObject();
            ob.put("metadata", up.getMetadata());
            ob.put("data", up.getData());

            json.put("update", ob);
            json.put("code", 200);

        }

        return json;

    }


    @ResponseBody
    @RequestMapping(value="/get_drive_id", method = RequestMethod.POST)
    public JSONObject getDriveId(String fch_addr) {

        JSONObject json = new JSONObject();

        if (StringUtils.isEmpty(fch_addr)) {
            json.put("code", 400);
            json.put("msg", "参数错误");
            return json;
        }

        FchXsvLink fchXsvLink = fchXsvLinkService.findByFch(fch_addr);

        if (fchXsvLink != null) {

            List<AddressDriveLink> addressDriveLink = addressDriveLinkService.findByAddress(fchXsvLink.getAddressHash(), 0);
            List<String> list = new ArrayList<>();
            for (AddressDriveLink ad : addressDriveLink) {
                list.add(ad.getDriveId());
            }

            json.put("code", 200);
            json.put("drive_id", list);

        } else {

            json.put("code", 200);
            json.put("drive_id", "");

        }

        return json;

    }

}

