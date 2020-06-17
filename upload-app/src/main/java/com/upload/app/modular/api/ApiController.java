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

    @Autowired
    private BalanceHistoryService balanceHistoryService;

    final Base64.Decoder decoder = Base64.getDecoder();

    final String tokenId = "c7f7c99fb2f9ad865ba17f702dc21e2643ac1562941888952a27aa399e261101";

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

        List<ScriptUtxoTokenLink> scriptUtxoTokenList = scriptUtxoTokenLinkService.findListByScript(scriptList, fchXsvLink.getAddressHash());

        BigInteger toAssets = scriptTokenLinkService.findToTokenByScript(scriptList);
        BigInteger fromAssets = scriptTokenLinkService.findFromTokenByScript(scriptList);
        BigInteger balance = toAssets.subtract(fromAssets);

        BigInteger sumAmount = new BigInteger("0");
        for (ScriptUtxoTokenLink sut : scriptUtxoTokenList) {
            BigInteger amount = scriptTokenLinkService.selectFAToken(tokenId, sut.getTxid(), sut.getN());
            sumAmount = sumAmount.add(amount);
        }


        if (balance.compareTo(new BigInteger("1000000000")) < 0) {
            result.put("code", 200212);
            result.put("msg", "用户积分不足");
            return result;
        }

        if (balance.compareTo(sumAmount) != 0) {
            result.put("code", 200213);
            result.put("msg", "token余额和链上不对应请稍后重试");
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
            Boolean f = blockchainPaymentService.payment(ad,1, "put");         // 查询钱,付费
            if (f != null && !f) {
                result.put("code", 100457);
                result.put("msg", "付费失败");
                return result;
            }


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

        Create create = createService.findByDriveId(drive_id);

        if (create != null) {
            if (create.getStatus() == 1) {
                result.put("code", 100456);
                result.put("msg", "该driveid已经结束");
                return result;
            }
        } else {
            result.put("code", 1002);
            result.put("msg", "该drive_id不存在");
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

        List<String> scriptList = addressScriptLinkService.findListByAddress(fxl.getAddressHash());

        if (scriptList == null || scriptList.size() < 1) {
            result.put("code", 200212);
            result.put("msg", "用户积分不足");
            return result;
        }

        List<ScriptUtxoTokenLink> scriptUtxoTokenList = scriptUtxoTokenLinkService.findListByScript(scriptList, fxl.getAddressHash());

        BigInteger toAssets = scriptTokenLinkService.findToTokenByScript(scriptList);
        BigInteger fromAssets = scriptTokenLinkService.findFromTokenByScript(scriptList);
        BigInteger balance = toAssets.subtract(fromAssets);

        BigInteger sumAmount = new BigInteger("0");
        for (ScriptUtxoTokenLink sut : scriptUtxoTokenList) {
            BigInteger amount = scriptTokenLinkService.selectFAToken(tokenId, sut.getTxid(), sut.getN());
            sumAmount = sumAmount.add(amount);
        }


        if (balance.compareTo(new BigInteger("1000000000")) < 0) {
            result.put("code", 200212);
            result.put("msg", "用户积分不足");
            return result;
        }

        if (balance.compareTo(sumAmount) != 0) {
            result.put("code", 200213);
            result.put("msg", "token余额和链上不对应请稍后重试");
            return result;
        }

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

            Boolean f = blockchainPaymentService.payment(fchadd,1, "update");         // 查询钱,付费
            if (f != null && !f) {
                result.put("code", 100457);
                result.put("msg", "付费失败");
                return result;
            }

        } else {

            result.put("code", 500);
            result.put("msg", "处理异常,联系客服");

        }

        return result;

    }


    @ResponseBody
    @RequestMapping(value="/get", method = RequestMethod.POST)
    public JSONObject get(String fch_addr, String drive_id, String update_id) throws Exception {

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


        List<String> scriptList = addressScriptLinkService.findListByAddress(fchXsvLink.getAddressHash());

        if (scriptList == null || scriptList.size() < 1) {
            json.put("code", 200212);
            json.put("msg", "用户积分不足");
            return json;
        }

        List<ScriptUtxoTokenLink> scriptUtxoTokenList = scriptUtxoTokenLinkService.findListByScript(scriptList, fchXsvLink.getAddressHash());


        BigInteger toAssets = scriptTokenLinkService.findToTokenByScript(scriptList);
        BigInteger fromAssets = scriptTokenLinkService.findFromTokenByScript(scriptList);
        BigInteger balance = toAssets.subtract(fromAssets);

        BigInteger sumAmount = new BigInteger("0");
        for (ScriptUtxoTokenLink sut : scriptUtxoTokenList) {
            BigInteger amount = scriptTokenLinkService.selectFAToken(tokenId, sut.getTxid(), sut.getN());
            sumAmount = sumAmount.add(amount);
        }


        if (balance.compareTo(new BigInteger("200000000")) < 0) {
            json.put("code", 200212);
            json.put("msg", "用户积分不足");
            return json;
        }

        if (balance.compareTo(sumAmount) != 0) {
            json.put("code", 200213);
            json.put("msg", "token余额和链上不对应请稍后重试");
            return json;
        }


        Boolean f = blockchainPaymentService.payment(fch_addr,2, "get");         // 查询钱,付费
        if (f != null && !f) {
            json.put("code", 100457);
            json.put("msg", "付费失败");
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
    public JSONObject getDriveId(String fch_addr) throws Exception {

        JSONObject json = new JSONObject();

        if (StringUtils.isEmpty(fch_addr)) {
            json.put("code", 400);
            json.put("msg", "参数错误");
            return json;
        }

        FchXsvLink fchXsvLink = fchXsvLinkService.findByFch(fch_addr);

        List<String> scriptList = addressScriptLinkService.findListByAddress(fchXsvLink.getAddressHash());

        if (scriptList == null || scriptList.size() < 1) {
            json.put("code", 200212);
            json.put("msg", "用户积分不足");
            return json;
        }

        List<ScriptUtxoTokenLink> scriptUtxoTokenList = scriptUtxoTokenLinkService.findListByScript(scriptList, fchXsvLink.getAddressHash());

        BigInteger toAssets = scriptTokenLinkService.findToTokenByScript(scriptList);
        BigInteger fromAssets = scriptTokenLinkService.findFromTokenByScript(scriptList);
        BigInteger balance = toAssets.subtract(fromAssets);

        BigInteger sumAmount = new BigInteger("0");
        for (ScriptUtxoTokenLink sut : scriptUtxoTokenList) {
            BigInteger amount = scriptTokenLinkService.selectFAToken(tokenId, sut.getTxid(), sut.getN());
            sumAmount = sumAmount.add(amount);
        }

        if (balance.compareTo(new BigInteger("200000000")) < 0) {
            json.put("code", 200212);
            json.put("msg", "用户积分不足");
            return json;
        }

        Boolean f = blockchainPaymentService.payment(fch_addr,2, "get_drive_id");         // 查询钱,付费
        if (f != null && !f) {
            json.put("code", 100457);
            json.put("msg", "付费失败");
            return json;
        }

        if (balance.compareTo(sumAmount) != 0) {
            json.put("code", 200213);
            json.put("msg", "token余额和链上不对应请稍后重试");
            return json;
        }

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

    @ResponseBody
    @RequestMapping(value="/get_balance", method = RequestMethod.POST)
    public JSONObject query(String fch_addr) throws Exception {

        JSONObject ob = new JSONObject();

        if ("F".equals(fch_addr) || "f".equals(fch_addr)) {
            fch_addr = Api.fchtoxsv(fch_addr).getString("address");
        } else if ("1".equals(fch_addr)) {
            fch_addr = fch_addr;
        }

        FchXsvLink fchXsvLink = fchXsvLinkService.findByFch(fch_addr);

        List<String> scriptList = addressScriptLinkService.findListByAddress(fchXsvLink.getAddressHash());


        if (scriptList == null || scriptList.size() < 1) {
            ob.put("balance", 0);
            ob.put("code", 200);
            return ob;
        }

        List<ScriptUtxoTokenLink> scriptUtxoTokenList = scriptUtxoTokenLinkService.findListByScript(scriptList, fchXsvLink.getAddressHash());
        BigInteger balance = new BigInteger("0");

        for (ScriptUtxoTokenLink sut : scriptUtxoTokenList) {
            BigInteger amount = scriptTokenLinkService.selectFAToken(tokenId, sut.getTxid(), sut.getN());
            balance = balance.add(amount);
        }


        ob.put("balance", balance.divide(new BigInteger("100000000")));
        ob.put("code", 200);

        return ob;

    }


    @ResponseBody
    @RequestMapping(value="/get_tx_history", method = RequestMethod.POST)
    public JSONObject getTxHistory(String fch_addr) {

        JSONObject ob = new JSONObject();
        List<BalanceHistory> balanceHistoryList = balanceHistoryService.findByAddress(fch_addr);
        ob.put("data", balanceHistoryList);
        ob.put("code", 200);
        return ob;

    }

    @ResponseBody
    @RequestMapping(value="/terminate_drive_id", method = RequestMethod.POST)
    public JSONObject terminateDriveId(@RequestBody String json) throws Exception {

        JSONObject result = new JSONObject();

        JSONObject param = null;
        try {
            param = (JSONObject) JSONObject.parse(json);
        } catch (Exception e) {
            result.put("code",1009);
            result.put("msg","请传正确的json数据");
            return result;
        }

        String fch_addr = param.getString("fch_addr");
        String drive_id = param.getString("drive_id");


        Create create = createService.findByDriveId(drive_id);

        if (create != null) {
            if (create.getStatus() == 1) {
                result.put("code", 100456);
                result.put("msg", "该driveid已经结束");
                return result;
            }
        } else {
            result.put("code", 1002);
            result.put("msg", "该drive_id不存在");
            return result;
        }

        FchXsvLink fchXsvLink = fchXsvLinkService.findByFch(fch_addr);

        List<String> scriptList = addressScriptLinkService.findListByAddress(fchXsvLink.getAddressHash());

        if (scriptList == null || scriptList.size() < 1) {
            result.put("code", 200212);
            result.put("msg", "用户积分不足");
            return result;
        }

        List<ScriptUtxoTokenLink> scriptUtxoTokenList = scriptUtxoTokenLinkService.findListByScript(scriptList, fchXsvLink.getAddressHash());
        BigInteger toAssets = scriptTokenLinkService.findToTokenByScript(scriptList);
        BigInteger fromAssets = scriptTokenLinkService.findFromTokenByScript(scriptList);
        BigInteger balance = toAssets.subtract(fromAssets);

        BigInteger sumAmount = new BigInteger("0");
        for (ScriptUtxoTokenLink sut : scriptUtxoTokenList) {
            BigInteger amount = scriptTokenLinkService.selectFAToken(tokenId, sut.getTxid(), sut.getN());
            sumAmount = sumAmount.add(amount);
        }

        if (balance.compareTo(new BigInteger("1000000000")) < 0) {
            result.put("code", 200212);
            result.put("msg", "用户积分不足");
            return result;
        }

        Boolean f = blockchainPaymentService.payment(fch_addr,2, "get_drive_id");         // 查询钱,付费
        if (f != null && !f) {
            result.put("code", 100457);
            result.put("msg", "付费失败");
            return result;
        }

        if (balance.compareTo(sumAmount) != 0) {
            result.put("code", 200213);
            result.put("msg", "token余额和链上不对应请稍后重试");
            return result;
        }

        List<AddressDriveLink> ad = addressDriveLinkService.findDriveByAddress(fchXsvLink.getAddressHash());
        if (ad.size() <= 0) {
            result.put("code", 1000);
            result.put("msg", "当前操作没有权限");
            return result;
        }

        AddressDriveLink addressDriveLink = addressDriveLinkService.findByAddressAndDriveId(fchXsvLink.getAddressHash(), drive_id);
        boolean flag = false;
        if (addressDriveLink != null)
            flag = true;

        if (!flag) {
            result.put("code", 1000);
            result.put("msg", "当前操作没有权限");
            return result;
        }

        List<String> fchAddress = new ArrayList<>();

        if (fchXsvLink != null)
            fchXsvLink.getXsvAddress();
        else {
            String xsvaddress = null;
            FchXsvLink insert = new FchXsvLink();
            if ("F".equals(fch_addr) || "f".equals(fch_addr)) {
                xsvaddress = Api.fchtoxsv(fch_addr).getString("address");
                insert.setType(0);
            } else if ("1".equals(fch_addr)) {
                xsvaddress = fch_addr;
                insert.setType(1);
            }
            insert.setFchAddress(fch_addr);
            insert.setXsvAddress(xsvaddress);
            String addressHash = Api.ValidateAddress(xsvaddress).getString("scriptPubKey").replaceFirst("76a914", "").replaceFirst("88ac", "");
            insert.setAddressHash(addressHash);
            fchXsvLinkService.insert(insert);
            fchXsvLink = fchXsvLinkService.findByFch(fch_addr);
        }
        fchAddress.add(fchXsvLink.getXsvAddress());

        DriveUtxo du = driveUtxoService.findByDriveId(drive_id);
        if (du == null) {
            result.put("1002", "该drive_id不存在");
            return result;
        }

        Boolean b = systemUtxoService.terminateDrive(fch_addr, drive_id);   //1表示create

        if (b) {

            result.put("code", 200);
            result.put("msg", "");

            Boolean a = blockchainPaymentService.payment(fch_addr,1, "terminate_drive_id");         // 查询钱,付费
            if (fchAddress != null && !a) {
                result.put("code", 100457);
                result.put("msg", "付费失败");
                return result;
            }

        } else {

            result.put("code", 500);
            result.put("msg", "处理异常,联系客服");

        }

        return result;

    }

}

