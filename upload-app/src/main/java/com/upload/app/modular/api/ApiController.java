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


//   /* *//**
//     * 上传接口
//     * @return
//     * @throws Exception
//     *//*
//    @ResponseBody
//    @RequestMapping(value="/upload", method = RequestMethod.POST)
//    public JsonResult getBalanceHistory(@RequestParam("fileName") MultipartFile file, String access_key, String tnonce, String signature) throws Exception {
//
//        if (access_key == null ||tnonce == null || signature == null)
//            return new JsonResult(BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getCode(), BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getMessage());
//
//        Long tonceLong = Long.valueOf(tnonce) + 150000;
//        if (System.currentTimeMillis() > tonceLong)
//            return new JsonResult(BizExceptionEnum.API_TIME_OUT_ERROR.getCode(), BizExceptionEnum.API_TIME_OUT_ERROR.getMessage());
//
//        Access access = accessService.findByAccessKey(access_key);
//        TreeMap<String, String> queryParas  = new TreeMap<>();
//        queryParas.put("access_key", access_key);
//        queryParas.put("tnonce", tnonce);
//        String sign = EncryptUtil.sha256_HMAC(queryParas ,"/api/upload", access.getKey());
//
//        if (!signature.equals(sign))
//            return new JsonResult(BizExceptionEnum.API_SIGN_ERROR.getCode(), BizExceptionEnum.API_SIGN_ERROR.getMessage());
//
//
//        String deposeFilesDir = "/java/upload/data/";
//
//        Upload upload = new Upload();
//
//        String fileName = file.getOriginalFilename();
//        upload.setName(fileName);
//
//        String type = file.getContentType();                      //文件类型
//        Long size = file.getSize();                             //文件大小
//
//
//
//
//
//        if (size > 1024 * 1024 * 30) {
//            System.out.println("文件大小为(单位字节):" + size);
//            System.out.println("该文件大于30M");
//        }
//
//        if (fileName.indexOf(".") >= 0) {
//            // split()中放正则表达式; 转义字符"\\."代表 "."
//            String[] fileNameSplitArray = fileName.split("\\.");
//            // 加上random戳,防止附件重名覆盖原文件
//            fileName = fileNameSplitArray[0] + (int) (Math.random() * 100000) + "." + fileNameSplitArray[1];
//        } else {
//            // 加上random戳,防止附件重名覆盖原文件
//            fileName = fileName + (int) (Math.random() * 100000);
//        }
//
//        File dest = new File(deposeFilesDir + fileName);
//
//        if (!dest.getParentFile().exists()) {
//            dest.getParentFile().mkdirs();
//        }
//
//        try {
//            // 将获取到的附件file,transferTo写入到指定的位置(即:创建dest时，指定的路径)
//            file.transferTo(dest);
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//
//        upload.setOpenId(access.getOpenid());
//        upload.setUrl(deposeFilesDir);
//        upload.setFullname(fileName);
//        upload.setFileId(UUID.randomUUID().toString().replaceAll("-",""));
//        uploadService.insertUpload(upload);
//
//        return new JsonResult().addData("data", deposeFilesDir + fileName);
//
//    }*/




//    /**
//     * 上传接口
//     * @return
//     * @throws Exception
//     */
//    @ResponseBody
//    @RequestMapping(value="/upload", method = RequestMethod.POST)
//    public JsonResult upload(@RequestParam("fileName") MultipartFile file, String access_key, String tnonce, String signature) throws Exception {
//
////        if (access_key == null ||tnonce == null || signature == null)
////            return new JsonResult(BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getCode(), BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getMessage());
////
////        Long tonceLong = Long.valueOf(tnonce) + 150000;
////        if (System.currentTimeMillis() > tonceLong)
////            return new JsonResult(BizExceptionEnum.API_TIME_OUT_ERROR.getCode(), BizExceptionEnum.API_TIME_OUT_ERROR.getMessage());
////
////        Access access = accessService.findByAccessKey(access_key);
////        TreeMap<String, String> queryParas  = new TreeMap<>();
////        queryParas.put("access_key", access_key);
////        queryParas.put("tnonce", tnonce);
////        String sign = EncryptUtil.sha256_HMAC(queryParas ,"/api/upload", access.getKey());
////
////        if (!signature.equals(sign))
////            return new JsonResult(BizExceptionEnum.API_SIGN_ERROR.getCode(), BizExceptionEnum.API_SIGN_ERROR.getMessage());
//
//
//        Upload upload = new Upload();
//
//        String fileName = file.getOriginalFilename();
//        upload.setName(fileName);
//
//        Long size = file.getSize();                             //文件大小
//        String type = getExtensionName(fileName);
//
////        String files = new String(file.getBytes(), "UTF-8");
//
//        if (size > 1024 * 1024 * 15) {
//            return new JsonResult(BizExceptionEnum.SIZE_ERROR.getCode(), BizExceptionEnum.SIZE_ERROR.getMessage());
//        }
//
//        String optrun = UnicodeUtil.bytesToHexString(file.getBytes());
//
//        String[] sysAddress = {systemAddress};
//
//        List<TxInputDto> input = new ArrayList<>();
//        Map query = new HashedMap();
//        query.put("address", "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
//        JSONObject utxo = (JSONObject) JSONObject.parse(HttpUtil.doPost("http://47.110.137.123:8433/rest/Api/getUtxo",query));
//        JSONObject udata = utxo.getJSONObject("data");
//        JSONArray utxos = udata.getJSONArray("utxo");
//
//        BigDecimal value = new BigDecimal("0");                 //总的utxo钱
//        for (Object ob : utxos) {
//            JSONObject ux = (JSONObject) ob;
//            TxInputDto in = new TxInputDto(ux.getString("txid"),ux.getInteger("n"),"");
//            value = value.add(new BigDecimal(ux.getString("value")));
//            input.add(in);
//        }
//
//        List<TxOutputDto> output = new ArrayList<>();
//
//
//
//
//        BigDecimal fee = new BigDecimal("0.00000001").multiply(new BigDecimal(size));
//        BigDecimal v = value.subtract(fee);
//
//        TxOutputDto dd = new TxOutputDto("1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx", v);				// 找零系统地址
//        output.add(dd);
//
//        TxOutputDto d = new TxOutputDto(optrun);
//        output.add(d);
//
//
//        String createRawTransaction = Api.CreateRawTransaction(input, output);
//
//        String signhex = Api.SignRawTransaction(createRawTransaction);
//
//        System.out.println(signhex);
//
//        String txid = Api.SendRawTransaction(signhex);
//
//        if (txid == null) {
//            return new JsonResult(BizExceptionEnum.TX_ERROR.getCode(), BizExceptionEnum.TX_ERROR.getMessage());
//        }
//
////        upload.setOpenId(access.getOpenid());
////        upload.setTxid(txid);
////        upload.setType(type);
////        uploadService.insertUpload(upload);
//
//        return new JsonResult().addData("txid", txid);
//
//    }
//
//    public static String getExtensionName(String filename) {
//        if ((filename != null) && (filename.length() > 0)) {
//            int dot = filename.lastIndexOf('.');
//            if ((dot >-1) && (dot < (filename.length() - 1))) {
//                return filename.substring(dot + 1);
//            }
//        }
//        return filename;
//    }

//
//    /**
//     * 文件列表
//     * @return
//     * @throws Exception
//     */
//    @ResponseBody
//    @RequestMapping(value="/getList", method = RequestMethod.POST)
//    public JsonResult getList(String access_key, String tnonce, String signature) {
//
//        if (access_key == null ||tnonce == null || signature == null)
//            return new JsonResult(BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getCode(), BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getMessage());
//
//        Long tonceLong = Long.valueOf(tnonce) + 150000;
//        if (System.currentTimeMillis() > tonceLong)
//            return new JsonResult(BizExceptionEnum.API_TIME_OUT_ERROR.getCode(), BizExceptionEnum.API_TIME_OUT_ERROR.getMessage());
//
//        Access access = accessService.findByAccessKey(access_key);
//        TreeMap<String, String> queryParas  = new TreeMap<>();
//        queryParas.put("access_key", access_key);
//        queryParas.put("tnonce", tnonce);
//        String sign = EncryptUtil.sha256_HMAC(queryParas ,"/api/getList", access.getKey());
//
//        if (!signature.equals(sign))
//            return new JsonResult(BizExceptionEnum.API_SIGN_ERROR.getCode(), BizExceptionEnum.API_SIGN_ERROR.getMessage());
//
//        List<Upload> list = uploadService.findListByOpenId(access.getOpenid());
//        return new JsonResult().addData("data", list);
//
//    }
//
//    @RequestMapping("/download")
//    public void download1(HttpServletResponse response, String txid, String access_key, String tnonce, String signature) throws Exception {
//
//        Access access = accessService.findByAccessKey(access_key);
//        TreeMap<String, String> queryParas  = new TreeMap<>();
//        queryParas.put("access_key", access_key);
//        queryParas.put("tnonce", tnonce);
//        String sign = EncryptUtil.sha256_HMAC(queryParas ,"/api/download", access.getKey());
//
//        Long tonceLong = Long.valueOf(tnonce) + 150000;
//
//        if (System.currentTimeMillis() < tonceLong && signature.equals(sign)) {
//            JSONObject json = Api.GetRawTransaction(txid);
//            JSONArray vouts = json.getJSONArray("vout");
//            for (int i = 0; i < vouts.size(); i++) {
//                JSONObject ob = (JSONObject) vouts.get(i);
//                int n = ob.getInteger("n");
//                if (n == vouts.size() - 1) {
//                    JSONObject scriptPubKey = ob.getJSONObject("scriptPubKey");
//                    String op_return = scriptPubKey.getString("asm");
//
//                    String op = op_return.replaceFirst("0 OP_RETURN ", "");
//
//                    Upload upload = uploadService.findByName(txid, access.getOpenid());
//
//                    byte[] bt = null;
//
//                    if (upload.getType().equals("txt")) {
//
//                        String data = UnicodeUtil.hexStringToString(op);
//                        bt = data.getBytes();
//
//                    } else {
//
//                        bt = UnicodeUtil.hexStringToBytes(op);
//
//                    }
//
//                    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(upload.getName(), "UTF-8"));
//
//                    byte[] buff = new byte[1024];
//                    BufferedInputStream bufferedInputStream = null;
//                    OutputStream outputStream = null;
//                    try {
//                        outputStream = response.getOutputStream();
//
//                        bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(bt), bt.length);
//                        int num = bufferedInputStream.read(buff);
//                        while (num != -1) {
//                            outputStream.write(buff, 0, num);
//                            outputStream.flush();
//                            num = bufferedInputStream.read(buff);
//                        }
//                    } catch (IOException e) {
//                        throw new RuntimeException(e.getMessage());
//                    } finally {
//                        if (bufferedInputStream != null) {
//                            bufferedInputStream.close();
//                        }
//                    }
//
//                }
//            }
//        }
//
//    }


//    @RequestMapping("/download")
//    public void download1(HttpServletResponse response, String fileId, String access_key, String tnonce, String signature) {
//
//        Access access = accessService.findByAccessKey(access_key);
//        TreeMap<String, String> queryParas  = new TreeMap<>();
//        queryParas.put("access_key", access_key);
//        queryParas.put("tnonce", tnonce);
//        String sign = EncryptUtil.sha256_HMAC(queryParas ,"/api/download", access.getKey());
//
//        Long tonceLong = Long.valueOf(tnonce) + 150000;
//
//        if (System.currentTimeMillis() < tonceLong && signature.equals(sign)) {
//
//            Upload uplod = uploadService.findByName(fileId, 1001);
//            String fileName = uplod.getUrl() + uplod.getFullname();
//            Path file = Paths.get(fileName);
//            if (Files.exists(file)) {
//                response.setContentType("application/x-gzip");
//                try {
//                    response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(uplod.getName(), "UTF-8"));
//                    Files.copy(file, response.getOutputStream());
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//        }
//
//
//    }


//    @RequestMapping("/downloads")
//    public void downloads(HttpServletResponse response, String name, String access_key, String tnonce, String signature) {
//
//        Access access = accessService.findByAccessKey(access_key);
//        TreeMap<String, String> queryParas  = new TreeMap<>();
//        queryParas.put("access_key", access_key);
//        queryParas.put("tnonce", tnonce);
//        String sign = EncryptUtil.sha256_HMAC(queryParas ,"/api/download", access.getKey());
//
//        Long tonceLong = Long.valueOf(tnonce) + 150000;
//
//        if (System.currentTimeMillis() < tonceLong && signature.equals(sign)) {
//
//            Upload uplod = uploadService.findByName(name, 1001);
//            String fileName = uplod.getUrl() + uplod.getFullname();
//            Path file = Paths.get(fileName);
//            if (Files.exists(file)) {
//                response.setContentType("application/x-gzip");
//                try {
//                    response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
//                    Files.copy(file, response.getOutputStream());
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//        }
//
//
//    }



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

        fchAddress.add("1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        String[] address = fchAddress.toArray(new String[0]);
        CommonTxOputDto c1 = new CommonTxOputDto(address, new BigDecimal("0.00001"), metadata, 1);
        outputs.add(c1);


        BigDecimal fee = new BigDecimal("0.00000001").multiply(new BigDecimal(size)).divide(new BigDecimal("2"));

        BigDecimal fvalue = v.subtract(fee).subtract(new BigDecimal("0.00002"));
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

        fchAddress.add("1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        String[] address = fchAddress.toArray(new String[0]);
        CommonTxOputDto c1 = new CommonTxOputDto(address, new BigDecimal("0.00001"), metadata, 1);
        outputs.add(c1);


        BigDecimal fee = new BigDecimal("0.00000001").multiply(new BigDecimal(size)).divide(new BigDecimal("2"));

        BigDecimal fvalue = v.subtract(fee).subtract(new BigDecimal("0.00002"));
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

