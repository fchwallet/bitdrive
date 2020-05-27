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

import com.upload.app.core.util.*;

import com.upload.app.modular.system.model.*;
import com.upload.app.modular.system.service.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    final Base64.Decoder decoder = Base64.getDecoder();

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

        JSONObject param = (JSONObject) JSONObject.parse(json);

        JSONArray fch_addr = param.getJSONArray("fch_addr");

        String metadata = param.getString("metadata");

        String data = param.getString("data");
        String signautre = param.getString("signature");

        if (fch_addr == null || fch_addr.size() < 0 || StringUtils.isEmpty(metadata) || StringUtils.isEmpty(data) || StringUtils.isEmpty(signautre)) {
            result.put("code", 400);
            result.put("msg", "参数错误");
            return result;
        }


        String fchXSVaddress = Api.fchtoxsv(fch_addr.getString(0)).getString("address");
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

        Map query = new HashedMap();
        query.put("address", "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        JSONObject utxo = (JSONObject) JSONObject.parse(HttpUtil.doPost("http://47.110.137.123:8433/rest/Api/getUtxo",query));
        JSONObject udata = utxo.getJSONObject("data");
        JSONArray utxos = udata.getJSONArray("utxo");
        List<TxInputDto> inputs = new ArrayList<>();
        BigDecimal v = new BigDecimal("0");                 //总的utxo钱
        for (Object ob : utxos) {
            JSONObject ux = (JSONObject) ob;
            TxInputDto input = new TxInputDto(ux.getString("txid"),ux.getInteger("n"),"");
            v = v.add(new BigDecimal(ux.getString("value")));
            inputs.add(input);
        }

        List<CommonTxOputDto> outputs = new ArrayList<>();


        List<String> fchAddress = new ArrayList<>();
        for (Object o : fch_addr) {

            String addr = (String) o;
            FchXsvLink f = fchXsvLinkService.findByFch(addr);

            if (f != null)
                f.getXsvAddress();
            else {
                String xsvaddress = Api.fchtoxsv(addr).getString("address");
                FchXsvLink insert = new FchXsvLink();
                insert.setFchAddress(addr);
                insert.setXsvAddress(xsvaddress);
                String addressHash = Api.ValidateAddress(xsvaddress).getString("scriptPubKey").replaceFirst("76a914", "").replaceFirst("88ac", "");
                insert.setAddressHash(addressHash);
                fchXsvLinkService.insert(insert);
                f = fchXsvLinkService.findByFch(addr);
            }

            fchAddress.add(f.getXsvAddress());

        }

        Integer metadatasize = metadata.getBytes().length;
        BigDecimal metadatafee = new BigDecimal("0.00000001").multiply(new BigDecimal(metadatasize)).divide(new BigDecimal("2"));
        if (metadatafee.compareTo(new BigDecimal("0.00002")) < 0)
            metadatafee = new BigDecimal("0.00002");

        fchAddress.add("1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        String[] address = fchAddress.toArray(new String[0]);
        CommonTxOputDto c1 = new CommonTxOputDto(address, metadatafee, metadata, 1);
        outputs.add(c1);


        BigDecimal fee = new BigDecimal("0.00000001").multiply(new BigDecimal(size)).divide(new BigDecimal("2"));

        BigDecimal fvalue = v.subtract(fee).subtract(new BigDecimal("0.00001").subtract(metadatafee));
        String[] sysad = {"1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx"};
        CommonTxOputDto c2 = new CommonTxOputDto(sysad, fvalue, 2);
        outputs.add(c2);                                //找零


        CommonTxOputDto c3 = new CommonTxOputDto(data, 3);
        outputs.add(c3);                            //文件


        String createHex = Api.CreateDrivetx(inputs, outputs);
        String signHex = Api.SignDrivetx(createHex, "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        String a = Api.SendRawTransaction(signHex);

        JSONArray put = new JSONArray();
        put.add(a);

        String drive_id = decodeService.decodeCreate(put, null);

        result.put("code", 200);
        result.put("drive_id",drive_id);
        return result;

    }


    @ResponseBody
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public JSONObject update(@RequestBody String json) throws Exception {

        JSONObject result = new JSONObject();

//        JSONObject j = (JSONObject) JSONObject.parse(json);

//        String plaintext = (new String(decoder.decode(j.getString("value")), "UTF-8"));

        JSONObject param = (JSONObject) JSONObject.parse(json);

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

        String fchXSVaddress = Api.fchtoxsv(fch_addr.getString(0)).getString("address");
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

        Map query = new HashedMap();
        query.put("address", "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        JSONObject utxo = (JSONObject) JSONObject.parse(HttpUtil.doPost("http://47.110.137.123:8433/rest/Api/getUtxo",query));
        JSONObject udata = utxo.getJSONObject("data");
        JSONArray utxos = udata.getJSONArray("utxo");
        List<TxInputDto> inputs = new ArrayList<>();
        BigDecimal v = new BigDecimal("0");                 //总的utxo钱
        for (Object ob : utxos) {
            JSONObject ux = (JSONObject) ob;
            TxInputDto input = new TxInputDto(ux.getString("txid"),ux.getInteger("n"),"");
            v = v.add(new BigDecimal(ux.getString("value")));
            inputs.add(input);
        }

        DriveUtxo du = driveUtxoService.findByDriveId(drive_id);
        if (du == null) {
            result.put("1000", "utxo遗失");
            return result;
        }

        TxInputDto input = new TxInputDto(du.getTxid(), du.getN(),"");
        inputs.add(input);
        v = v.add(new BigDecimal(du.getValue()));

        List<CommonTxOputDto> outputs = new ArrayList<>();


        List<String> fchAddress = new ArrayList<>();
        for (Object o : fch_addr) {
            String addr = (String) o;
            FchXsvLink f = fchXsvLinkService.findByFch(addr);
            if (f != null)
                f.getXsvAddress();
            else {
                String xsvaddress = Api.fchtoxsv(addr).getString("address");
                FchXsvLink insert = new FchXsvLink();
                insert.setFchAddress(addr);
                insert.setXsvAddress(xsvaddress);
                String addressHash = Api.ValidateAddress(xsvaddress).getString("scriptPubKey").replaceFirst("76a914", "").replaceFirst("88ac", "");
                insert.setAddressHash(addressHash);
                fchXsvLinkService.insert(insert);
                f = fchXsvLinkService.findByFch(addr);
            }
            fchAddress.add(f.getXsvAddress());
        }

        Integer metadatasize = metadata.getBytes().length;
        BigDecimal metadatafee = new BigDecimal("0.00000001").multiply(new BigDecimal(metadatasize)).divide(new BigDecimal("2"));
        if (metadatafee.compareTo(new BigDecimal("0.00002")) < 0)
            metadatafee = new BigDecimal("0.00002");

        fchAddress.add("1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        String[] address = fchAddress.toArray(new String[0]);
        CommonTxOputDto c1 = new CommonTxOputDto(address, metadatafee, metadata, 1);
        outputs.add(c1);


        BigDecimal fee = new BigDecimal("0.00000001").multiply(new BigDecimal(size)).divide(new BigDecimal("2"));

        BigDecimal fvalue = v.subtract(fee).subtract(new BigDecimal("0.00001")).subtract(metadatafee);
        String[] sysad = {"1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx"};
        CommonTxOputDto c2 = new CommonTxOputDto(sysad, fvalue, 2);
        outputs.add(c2);                                //找零


        CommonTxOputDto c3 = new CommonTxOputDto(data, 3);
        outputs.add(c3);                            //文件


        String createHex = Api.CreateDrivetx(inputs, outputs);
        String signHex = Api.SignDrivetx(createHex, "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        String a = Api.SendRawTransaction(signHex);

        JSONArray put = new JSONArray();
        put.add(a);

        String update_id = decodeService.decodeCreate(put, drive_id);

        result.put("code", 200);
        result.put("update_id",update_id);
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

        List<AddressDriveLink> addressDriveLink = addressDriveLinkService.findByAddress(fchXsvLink.getAddressHash());
        List<String> list = new ArrayList<>();
        for (AddressDriveLink ad : addressDriveLink) {
            list.add(ad.getDriveId());
        }

        json.put("code", 200);
        json.put("drive_id", list);
        return json;

    }

}

