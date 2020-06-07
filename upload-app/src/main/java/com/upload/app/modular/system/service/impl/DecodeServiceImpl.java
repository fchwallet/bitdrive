package com.upload.app.modular.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.upload.app.core.util.UnicodeUtil;
import com.upload.app.modular.system.dao.*;
import com.upload.app.modular.system.model.*;
import com.upload.app.modular.system.service.DecodeService;
import com.upload.app.core.rpc.Api;
import com.upload.app.core.util.Sha256;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class DecodeServiceImpl implements DecodeService {

    @Resource
    private CreateMapper createMapper;

    @Resource
    private FchXsvLinkMapper fchXsvLinkMapper;

    @Resource
    private UpdateMapper updateMapper;

    @Resource
    private AddressDriveLinkMapper addressDriveLinkMapper;

    @Resource
    private DriveTxAddressMapper driveTxAddressMapper;

    @Resource
    private DriveUtxoMapper driveUtxoMapper;

    static String deposeFilesDir = "C:\\Users\\caiyile\\Desktop\\test\\";

    @Override
    @Transactional(rollbackFor=Exception.class)
    public String decodeCreate(JSONArray jsonArray, String drive) throws Exception {

        for (Object ob : jsonArray) {

            String tx = (String)ob;

            Long count = driveTxAddressMapper.findByTxidCount(tx);

            if (count > 0)
                continue;

            try {

                JSONObject txTransaction = Api.GetRawTransaction(tx);

                authorityVins(txTransaction.getJSONArray("vin"));

                JSONArray vouts = txTransaction.getJSONArray("vout");

                StringBuilder data = new StringBuilder();

                JSONObject jb = new JSONObject();
                List<AddressDriveLink> addressDriveList = new ArrayList<>();

                String consValue = "";

                for (Object v : vouts) {

                    JSONObject vout = (JSONObject)v;

                    BigDecimal value = vout.getBigDecimal("value");

                    Integer voutn = vout.getInteger("n");

                    String n = UnicodeUtil.intToHex(voutn);

                    JSONObject scriptPubKey =  vout.getJSONObject("scriptPubKey");

                    String type = scriptPubKey.getString("type");

                    if ("nonstandard".equals(type)) {

                        String content = scriptPubKey.getString("hex");
                        String driveId = Sha256.getSHA256(tx+n);

                        String ifHex = content.substring(0, 2);

                        if (!"63".equals(ifHex)) {
                            continue;
                        }

                        content = content.replaceFirst(ifHex, "");

                        String oph = content.substring(0, 6);

                        if (!"76a914".equals(oph)) {
                            continue;
                        }

                        content = content.replaceFirst(oph, "");

                        String address1 = content.substring(0, 40);

                        FchXsvLink fchXsvLink1 = fchXsvLinkMapper.findByHash(address1);

                        if (fchXsvLink1 != null) {
                            if (!fchXsvLink1.getXsvAddress().equals("1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx")) {
                                AddressDriveLink addressDriveLink = new AddressDriveLink();
                                addressDriveLink.setAddress(fchXsvLink1.getAddressHash());
                                addressDriveLink.setDriveId(driveId);
                                addressDriveList.add(addressDriveLink);
                            }
                        }

                        content = content.replaceFirst(address1, "");

                        String opa = content.substring(0, 4);

                        if (!"88ac".equals(opa)) {
                            continue;
                        }

                        content = content.replaceFirst(opa, "");

                        String if67 = content.substring(0, 2);

                        Boolean falg = false;

                        if ("67".equals(if67)) {

                            String fs = list67(content, addressDriveList, driveId);

                            if (fs == null)
                                continue;
                            else {

                                content = fs;

                                while (true) {
                                    fs = list67(content, addressDriveList, driveId);
                                    if (fs == null)
                                        break;
                                    content = fs;
                                    fs = content.substring(0, 2);

                                    if (!"67".equals(fs))
                                        break;
                                }

                                if (content == null)
                                    continue;
                            }
                            falg = true;
                        }

                        String a68 = content.substring(0, 2);

                        if (!"68".equals(a68) && !falg){
                            continue;
                        }

                        content = content.replaceFirst(a68, "");

                        String a6 = content.substring(0, 2);

                        if (!a6.equals("6a"))
                            continue;

                        content = content.replaceFirst(a6, "");

                        String totalLength_Hex = content.substring(0, 2);

                        content = content.replaceFirst(totalLength_Hex, "");

                        if ("4c".equals(totalLength_Hex)) {

                            String length_hex = content.substring(0, 2);
                            Integer length = UnicodeUtil.decodeHEX(length_hex);
                            content = content.replaceFirst(length_hex, "");
                            content = content.substring(0, length*2);


                        } else if ("4d".equals(totalLength_Hex)) {

                            String length_hex = content.substring(0, 4);
                            String b = length_hex.substring(0,2);
                            String a = length_hex.substring(2,4);
                            String c = a+b;
                            Integer length = UnicodeUtil.decodeHEX(c);
                            content = content.replaceFirst(length_hex, "");
                            content = content.substring(0, length*2);

                        } else if ("4e".equals(totalLength_Hex)) {

                            String length_hex = content.substring(0, 8);
                            String b = length_hex.substring(0,4);
                            String a = length_hex.substring(4,8);
                            String c = a+b;
                            Integer length = UnicodeUtil.decodeHEX(c);
                            content = content.replaceFirst(length_hex, "");
                            content = content.substring(0, length*2);

                        } else {

                            Integer length = UnicodeUtil.decodeHEX(totalLength_Hex);
                            content = content.replaceFirst(totalLength_Hex, "");
                            content = content.substring(0, length*2);


                        }



                        String metadata = content;


                        jb.put("driveId", driveId);
                        jb.put("metadata", metadata);
                        jb.put("n", voutn);
                        jb.put("txid", tx);

                        consValue = value.toString();


                    } else if (value.compareTo(new BigDecimal("0")) == 0) {

                        String content = scriptPubKey.getString("hex");
                        content = content.replaceFirst("006a","");
                        String length_hex = content.substring(0,2);
                        content = content.replaceFirst(length_hex,"");

                        if ("4c".equals(length_hex)) {

                            Integer length = UnicodeUtil.decodeHEX(length_hex);
                            content = content.replaceFirst(length_hex,"");
                            String contentData = content.substring(0, length * 2);
                            data.append(contentData);
                            jb.put("data", contentData);

                        } else if ("4d".equals(length_hex)) {

                            length_hex = content.substring(0, 4);
                            String b = length_hex.substring(0,2);
                            String a = length_hex.substring(2,4);
                            String c = a+b;
                            Integer length = UnicodeUtil.decodeHEX(c);
                            content = content.replaceFirst(length_hex,"");
                            String contentData = content.substring(0, length * 2);
                            data.append(contentData);
                            jb.put("data", contentData);

                        } else if ("4e".equals(length_hex)) {

                            length_hex = content.substring(0, 8);
                            String b = length_hex.substring(0,4);
                            String a = length_hex.substring(4,8);
                            String c = a+b;
                            Integer length = UnicodeUtil.decodeHEX(c);
                            content = content.replaceFirst(length_hex, "");
                            String contentData = content.substring(0, length * 2);
                            data.append(contentData);
                            jb.put("data", contentData);

                        } else {

                            Integer length = UnicodeUtil.decodeHEX(length_hex);
                            String contentData = content.substring(0, length*2);
                            data.append(contentData);
                            jb.put("data", contentData);


                        }


                    }

                }

                for (AddressDriveLink addressDrive : addressDriveList) {

                    DriveTxAddress driveTxAddress = new DriveTxAddress();
                    driveTxAddress.setAddress(addressDrive.getAddress());
                    driveTxAddress.setN(jb.getInteger("n"));
                    driveTxAddress.setTxid(jb.getString("txid"));
                    driveTxAddress.setCreateDate(new Date());

                    if (drive != null && !"".equals(drive)) {
                        driveTxAddress.setDriveId(drive);
                        driveTxAddress.setUpdateId(jb.getString("driveId"));
                        addressDrive.setStatus(1);
                    } else {
                        driveTxAddress.setDriveId(jb.getString("driveId"));
                        addressDrive.setStatus(0);
                    }

                    driveTxAddressMapper.insert(driveTxAddress);
                    addressDriveLinkMapper.insert(addressDrive);

                }



                if (drive != null) {

                    DriveUtxo driveUtxo = new DriveUtxo();
                    driveUtxo.setN(jb.getInteger("n"));
                    driveUtxo.setTxid(jb.getString("txid"));
                    driveUtxo.setValue(consValue);
                    driveUtxo.setDriveId(drive);
                    driveUtxoMapper.insert(driveUtxo);

                    Update update = new Update();
                    update.setMetadata(jb.getString("metadata"));
                    update.setData(jb.getString("data"));
                    update.setDriveId(drive);
                    update.setUpdateId(jb.getString("driveId"));
                    updateMapper.insert(update);
                    return update.getUpdateId();
                } else {

                    DriveUtxo driveUtxo = new DriveUtxo();
                    driveUtxo.setN(jb.getInteger("n"));
                    driveUtxo.setTxid(jb.getString("txid"));
                    driveUtxo.setValue(consValue);
                    driveUtxo.setDriveId(jb.getString("driveId"));
                    driveUtxoMapper.insert(driveUtxo);

                    Create create = new Create();
                    create.setData(jb.getString("data"));
                    create.setMetadata(jb.getString("metadata"));
                    create.setDriveId(jb.getString("driveId"));
                    create.setTxid(jb.getString("txid"));
                    createMapper.insert(create);
                    return create.getDriveId();
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

        }

        return null;
    }

    @Override
    public void blockDecode(JSONArray jsonArray) throws Exception {

        for (Object ob : jsonArray) {

            String tx = (String)ob;

            Long count = driveTxAddressMapper.findByTxidCount(tx);

            if (count > 0)
                continue;

            try {

                JSONObject txTransaction = Api.GetRawTransaction(tx);

                List<String> driveList = authorityVinsList(txTransaction.getJSONArray("vin"));

                JSONArray vouts = txTransaction.getJSONArray("vout");

                StringBuilder data = new StringBuilder();

                JSONObject jb = new JSONObject();
                List<AddressDriveLink> addressDriveList = new ArrayList<>();

                String consValue = "";


                for (Object v : vouts) {

                    JSONObject vout = (JSONObject)v;

                    BigDecimal value = vout.getBigDecimal("value");

                    Integer voutn = vout.getInteger("n");

                    String n = UnicodeUtil.intToHex(voutn);

                    JSONObject scriptPubKey =  vout.getJSONObject("scriptPubKey");

                    String type = scriptPubKey.getString("type");

                    if ("nonstandard".equals(type)) {

                        String content = scriptPubKey.getString("hex");
                        String driveId = Sha256.getSHA256(tx+n);

                        String ifHex = content.substring(0, 2);

                        if (!"63".equals(ifHex)) {
                            continue;
                        }

                        content = content.replaceFirst(ifHex, "");

                        String oph = content.substring(0, 6);

                        if (!"76a914".equals(oph)) {
                            continue;
                        }

                        content = content.replaceFirst(oph, "");

                        String address1 = content.substring(0, 40);

                        FchXsvLink fchXsvLink1 = fchXsvLinkMapper.findByHash(address1);

                        if (fchXsvLink1 != null) {
                            if (!fchXsvLink1.getXsvAddress().equals("1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx")) {
                                AddressDriveLink addressDriveLink = new AddressDriveLink();
                                addressDriveLink.setAddress(fchXsvLink1.getAddressHash());
                                addressDriveLink.setDriveId(driveId);
                                addressDriveList.add(addressDriveLink);
                            }
                        }

                        content = content.replaceFirst(address1, "");

                        String opa = content.substring(0, 4);

                        if (!"88ac".equals(opa)) {
                            continue;
                        }

                        content = content.replaceFirst(opa, "");

                        String if67 = content.substring(0, 2);

                        Boolean falg = false;

                        if ("67".equals(if67)) {

                            String fs = list67(content, addressDriveList, driveId);

                            if (fs == null)
                                continue;
                            else {

                                content = fs;

                                while (true) {
                                    fs = list67(content, addressDriveList, driveId);
                                    if (fs == null)
                                        break;
                                    content = fs;
                                    fs = content.substring(0, 2);

                                    if (!"67".equals(fs))
                                        break;
                                }

                                if (content == null)
                                    continue;
                            }
                            falg = true;
                        }

                        String a68 = content.substring(0, 2);

                        if (!"68".equals(a68) && !falg){
                            continue;
                        }

                        content = content.replaceFirst(a68, "");

                        String a6 = content.substring(0, 2);

                        if (!a6.equals("6a"))
                            continue;

                        content = content.replaceFirst(a6, "");

                        String totalLength_Hex = content.substring(0, 2);

                        content = content.replaceFirst(totalLength_Hex, "");

                        if ("4c".equals(totalLength_Hex)) {

                            String length_hex = content.substring(0, 2);
                            Integer length = UnicodeUtil.decodeHEX(length_hex);
                            content = content.replaceFirst(length_hex, "");
                            content = content.substring(0, length*2);


                        } else if ("4d".equals(totalLength_Hex)) {

                            String length_hex = content.substring(0, 4);
                            String b = length_hex.substring(0,2);
                            String a = length_hex.substring(2,4);
                            String c = a+b;
                            Integer length = UnicodeUtil.decodeHEX(c);
                            content = content.replaceFirst(length_hex, "");
                            content = content.substring(0, length*2);

                        } else if ("4e".equals(totalLength_Hex)) {

                            String length_hex = content.substring(0, 8);
                            String b = length_hex.substring(0,4);
                            String a = length_hex.substring(4,8);
                            String c = a+b;
                            Integer length = UnicodeUtil.decodeHEX(c);
                            content = content.replaceFirst(length_hex, "");
                            content = content.substring(0, length*2);

                        } else {

                            Integer length = UnicodeUtil.decodeHEX(totalLength_Hex);
                            content = content.replaceFirst(totalLength_Hex, "");
                            content = content.substring(0, length*2);


                        }



                        String metadata = content;


                        jb.put("driveId", driveId);
                        jb.put("metadata", metadata);
                        jb.put("n", voutn);
                        jb.put("txid", tx);

                        consValue = value.toString();


                    } else if (value.compareTo(new BigDecimal("0")) == 0) {

                        String content = scriptPubKey.getString("hex");
                        content = content.replaceFirst("006a","");
                        String length_hex = content.substring(0,2);
                        content = content.replaceFirst(length_hex,"");

                        if ("4c".equals(length_hex)) {

                            Integer length = UnicodeUtil.decodeHEX(length_hex);
                            content = content.replaceFirst(length_hex,"");
                            String contentData = content.substring(0, length * 2);
                            data.append(contentData);
                            jb.put("data", contentData);

                        } else if ("4d".equals(length_hex)) {

                            length_hex = content.substring(0, 4);
                            String b = length_hex.substring(0,2);
                            String a = length_hex.substring(2,4);
                            String c = a+b;
                            Integer length = UnicodeUtil.decodeHEX(c);
                            content = content.replaceFirst(length_hex,"");
                            String contentData = content.substring(0, length * 2);
                            data.append(contentData);
                            jb.put("data", contentData);

                        } else if ("4e".equals(length_hex)) {

                            length_hex = content.substring(0, 8);
                            String b = length_hex.substring(0,4);
                            String a = length_hex.substring(4,8);
                            String c = a+b;
                            Integer length = UnicodeUtil.decodeHEX(c);
                            content = content.replaceFirst(length_hex, "");
                            String contentData = content.substring(0, length * 2);
                            data.append(contentData);
                            jb.put("data", contentData);

                        } else {

                            Integer length = UnicodeUtil.decodeHEX(length_hex);
                            String contentData = content.substring(0, length*2);
                            data.append(contentData);
                            jb.put("data", contentData);


                        }



                    }

                }

                for (AddressDriveLink addressDrive : addressDriveList) {

                    DriveTxAddress driveTxAddress = new DriveTxAddress();
                    driveTxAddress.setAddress(addressDrive.getAddress());
                    driveTxAddress.setN(jb.getInteger("n"));
                    driveTxAddress.setTxid(jb.getString("txid"));
                    driveTxAddress.setCreateDate(new Date());
                    if (driveList != null && driveList.size() > 0) {
                        driveTxAddress.setDriveId(driveList.get(0));
                        driveTxAddress.setUpdateId(jb.getString("driveId"));
                        addressDrive.setStatus(1);
                    } else {
                        driveTxAddress.setDriveId(jb.getString("driveId"));
                        addressDrive.setStatus(0);
                    }

                    driveTxAddressMapper.insert(driveTxAddress);
                    addressDriveLinkMapper.insert(addressDrive);

                }



                if (driveList != null && driveList.size() > 0) {

                    DriveUtxo driveUtxo = new DriveUtxo();
                    driveUtxo.setN(jb.getInteger("n"));
                    driveUtxo.setTxid(jb.getString("txid"));
                    driveUtxo.setValue(consValue);
                    driveUtxo.setDriveId(driveList.get(0));
                    driveUtxoMapper.insert(driveUtxo);

                    Update update = new Update();
                    update.setMetadata(jb.getString("metadata"));
                    update.setData(jb.getString("data"));
                    update.setDriveId(driveList.get(0));
                    update.setUpdateId(jb.getString("driveId"));
                    update.setCreateDate(new Date());
                    updateMapper.insert(update);


                } else {

                    if (jb.size() > 0) {
                        DriveUtxo driveUtxo = new DriveUtxo();
                        driveUtxo.setN(jb.getInteger("n"));
                        driveUtxo.setTxid(jb.getString("txid"));
                        driveUtxo.setValue(consValue);
                        driveUtxo.setDriveId(jb.getString("driveId"));
                        driveUtxoMapper.insert(driveUtxo);

                        Create create = new Create();
                        create.setData(jb.getString("data"));
                        create.setMetadata(jb.getString("metadata"));
                        create.setDriveId(jb.getString("driveId"));
                        create.setTxid(jb.getString("txid"));
                        create.setCreateDate(new Date());
                        createMapper.insert(create);

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

        }


    }


    public boolean authorityVins(JSONArray vins) {

        boolean flag = false;
        for (Object v: vins) {

            JSONObject vin = (JSONObject) v;
            String txid = vin.getString("txid");
            Integer vout = vin.getInteger("vout");

            DriveUtxo du = driveUtxoMapper.findByTxidAndN(txid, vout);
            if (du != null) {
                flag = true;
                DriveUtxo DriveUtxo = new DriveUtxo();
                DriveUtxo.setTxid(txid);
                DriveUtxo.setN(vout);
                driveUtxoMapper.delete(DriveUtxo);
            }

        }

        return flag;
    }


    public List<String> authorityVinsList(JSONArray vins) {

        List<String> driveList = new ArrayList<>();

        for (Object v: vins) {

            JSONObject vin = (JSONObject) v;
            String txid = vin.getString("txid");
            Integer vout = vin.getInteger("vout");

            DriveUtxo du = driveUtxoMapper.findByTxidAndN(txid, vout);
            if (du != null) {
                DriveUtxo DriveUtxo = new DriveUtxo();
                DriveUtxo.setTxid(txid);
                DriveUtxo.setN(vout);
                driveUtxoMapper.delete(DriveUtxo);
                driveList.add(du.getDriveId());
            }

        }

        return driveList;
    }

    public String list67(String content, List<AddressDriveLink> addressDriveList, String driveId) {

        String if67 = content.substring(0, 2);

        content = content.replaceFirst(if67, "");

        if ("67".equals(if67)) {

            String oph1 = content.substring(0, 6);

            if (!"76a914".equals(oph1)) {
                return null;
            }

            content = content.replaceFirst(oph1, "");
            String address2 = content.substring(0, 40);

            FchXsvLink fchXsvLink2 = fchXsvLinkMapper.findByHash(address2);

            if (fchXsvLink2 != null) {
                if (!fchXsvLink2.getXsvAddress().equals("1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx")) {
                    AddressDriveLink addressDriveLink = new AddressDriveLink();
                    addressDriveLink.setAddress(fchXsvLink2.getAddressHash());
                    addressDriveLink.setDriveId(driveId);
                    addressDriveList.add(addressDriveLink);
                }
            }

            content = content.replaceFirst(address2, "");

            String opa = content.substring(0, 4);

            if (!"88ac".equals(opa)) {
                return null;
            }

            content = content.replaceFirst(opa, "");

            return content;

        }

        return null;

    }


    public boolean ouputFile(String data, String fileName) throws Exception {

        File dest = new File(deposeFilesDir + fileName);

        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        BufferedOutputStream bos = null;

        try {

            bos = new BufferedOutputStream(new FileOutputStream(dest)) ;
            bos.write(data.getBytes());
            bos.close();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                bos.close();
            }
        }

        return true;

    }


}
