package com.xyz.upload.app.modular.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xyz.upload.app.core.rpc.Api;
import com.xyz.upload.app.core.util.Sha256;
import com.xyz.upload.app.core.util.UnicodeUtil;
import com.xyz.upload.app.modular.system.dao.AddressDriveLinkMapper;
import com.xyz.upload.app.modular.system.dao.CreateMapper;
import com.xyz.upload.app.modular.system.dao.FchXsvLinkMapper;
import com.xyz.upload.app.modular.system.dao.UpdateMapper;
import com.xyz.upload.app.modular.system.model.AddressDriveLink;
import com.xyz.upload.app.modular.system.model.Create;
import com.xyz.upload.app.modular.system.model.FchXsvLink;
import com.xyz.upload.app.modular.system.model.Update;
import com.xyz.upload.app.modular.system.service.DecodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Override
    @Transactional(rollbackFor=Exception.class)
    public String decodeCreate(JSONArray jsonArray) throws Exception {

        for (Object ob : jsonArray) {

            String tx = (String)ob;

            try {

                JSONObject txTransaction = Api.GetRawTransaction(tx);

                JSONArray vouts = txTransaction.getJSONArray("vout");
                JSONArray vins = txTransaction.getJSONArray("vin");

                StringBuilder data = new StringBuilder();

                for (Object v : vouts) {

                    JSONObject vout = (JSONObject)v;

                    BigDecimal value = vout.getBigDecimal("value");

                    if (value.compareTo(new BigDecimal("0")) == 0) {
                        JSONObject scriptPubKey = (JSONObject) vout.get("scriptPubKey");

                        String content = scriptPubKey.getString("hex");
                        data.append(content);
                    }
                }

                for (Object v : vouts) {

                    List<AddressDriveLink> addressDriveList = new ArrayList<>();

                    JSONObject vout = (JSONObject)v;

                    String n = UnicodeUtil.intToHex(vout.getInteger("n"));

                    JSONObject scriptPubKey = (JSONObject) vout.get("scriptPubKey");

                    String content = scriptPubKey.getString("hex");
                    String metadata = content;
                    String driveId = Sha256.getSHA256(tx+n);

                    Create create = new Create();
                    create.setDriveId(driveId);


                    String ifHex = content.substring(0, 2);

                    if (!"63".equals(ifHex)) {
                        continue;
                    }


                    create.setTxid(tx);

                    content = content.replaceFirst(ifHex, "");

                    String oph = content.substring(0, 6);

                    if (!"76a914".equals(oph)) {
                        continue;
                    }

                    content = content.replaceFirst(oph, "");

                    String address1 = content.substring(0, 40);

                    FchXsvLink fchXsvLink1 = fchXsvLinkMapper.findByHash(address1);

                    if (fchXsvLink1 != null) {
                        AddressDriveLink addressDriveLink = new AddressDriveLink();
                        addressDriveLink.setAddress(fchXsvLink1.getAddressHash());
                        addressDriveLink.setDriveId(driveId);
                        addressDriveList.add(addressDriveLink);
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

                    String protocol_type_hex = content.substring(0, 2);
                    Integer protocol_type = UnicodeUtil.decodeHEX(protocol_type_hex);
                    content = content.replaceFirst(protocol_type_hex, "");
                    String protocol_type_str = content.substring(0, protocol_type * 2);
                    content = content.replaceFirst(protocol_type_str, "");

                    String protocol_id_hex = content.substring(0, 2);
                    Integer protocol_id = UnicodeUtil.decodeHEX(protocol_id_hex);
                    content = content.replaceFirst(protocol_id_hex, "");
                    String protocol_id_str = content.substring(0, protocol_id * 2);
                    content = content.replaceFirst(protocol_id_str, "");

                    String protocol_version_hex = content.substring(0, 2);
                    Integer protocol_version = UnicodeUtil.decodeHEX(protocol_version_hex);
                    content = content.replaceFirst(protocol_version_hex, "");
                    String protocol_version_str = content.substring(0, protocol_version * 2);
                    content = content.replaceFirst(protocol_version_str, "");

                    String action_hex = content.substring(0, 2);
                    Integer action = UnicodeUtil.decodeHEX(action_hex);
                    content = content.replaceFirst(action_hex, "");
                    String action_str = content.substring(0, action * 2);
                    content = content.replaceFirst(action_str, "");

                    if ("435245415445".equals(action_str)) {                                        // 创建

                        String data_hash = content.substring(0, 2);
                        content = content.replaceFirst(data_hash, "");
                        String data_hash_str = content.substring(0, Integer.valueOf(data_hash) * 2);
                        content = content.replaceFirst(data_hash_str, "");

                        String encrypt_hex = content.substring(0, 2);
                        Integer encrypt = UnicodeUtil.decodeHEX(encrypt_hex);
                        content = content.replaceFirst(encrypt_hex, "");
                        String encrypt_str = content.substring(0, encrypt * 2);
                        content = content.replaceFirst(encrypt_str, "");


                        Integer encryptBoolean = Integer.valueOf(encrypt_str);
                        create.setEncrypt(encryptBoolean);


                        if (encryptBoolean == 0) {

                            if (!"".equals(content)) {
                                continue;
                            }

                        } else if (encryptBoolean == 1) {

                            String encrypted_pwd_hex = content.substring(0, 2);
                            Integer encrypted_pwd = UnicodeUtil.decodeHEX(encrypted_pwd_hex);
                            content = content.replaceFirst(encrypted_pwd_hex, "");
                            String encrypted_pwd_str = content.substring(0, encrypted_pwd * 2);
                            content = content.replaceFirst(encrypted_pwd_str, "");

                            Integer en_pw = encrypted_pwd_str.getBytes().length / 2;

                            if (en_pw < 4 || en_pw > 32)
                                continue;

                            if (!"".equals(content)) {
                                continue;
                            }

                            create.setEncryptedPwd(UnicodeUtil.hexStringToString(encrypted_pwd_str));

                        } else {
                            continue;
                        }

                        create.setData(data.toString());
                        create.setMetadata(metadata);
                        createMapper.insert(create);

                        for (AddressDriveLink ad : addressDriveList) {
                            addressDriveLinkMapper.insert(ad);
                        }

                        return driveId;

                    } else if ("555044415445".equals(action_str)) {                 //更新

                        String data_hash = content.substring(0, 2);
                        content = content.replaceFirst(data_hash, "");
                        String data_hash_str = content.substring(0, Integer.valueOf(data_hash) * 2);
                        content = content.replaceFirst(data_hash_str, "");

                        String drive_id_hex = content.substring(0, 2);
                        content = content.replaceFirst(drive_id_hex, "");
                        String drive_id_str = content.substring(0, Integer.valueOf(drive_id_hex) * 2);
                        content = content.replaceFirst(drive_id_str, "");

                        if (!"".equals(content)) {
                            continue;
                        }

                        Create createDrive = createMapper.findByDriveId(drive_id_str);

                        if (createDrive == null)
                            continue;

                        Boolean f = authorityVins(vins, drive_id_str);

                        if (!f)
                            return null;

                        Update update = new Update();
                        update.setDriveId(drive_id_str);
                        update.setData(data.toString());
                        update.setMetadata(metadata);
                        updateMapper.insert(update);

                        return drive_id_str;

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }


        }

        return null;
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
                AddressDriveLink addressDriveLink = new AddressDriveLink();
                addressDriveLink.setAddress(fchXsvLink2.getAddressHash());
                addressDriveLink.setDriveId(driveId);
                addressDriveList.add(addressDriveLink);
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


    public boolean authorityVins(JSONArray vins, String driveId) throws Exception {

        for (Object v: vins) {

            JSONObject vin = (JSONObject) v;

            JSONObject json = Api.GetRawTransaction(vin.getString("txid"));
            JSONArray vout = json.getJSONArray("vout");
            JSONObject vv = vout.getJSONObject(vin.getInteger("vout"));
            String addressHash = vv.getJSONObject("scriptPubKey").getString("hex").replaceFirst("76a914","").replaceFirst("88ac","");

            AddressDriveLink addressDrive = addressDriveLinkMapper.findByAddressAndDriveId(addressHash, driveId);

            if (addressDrive != null)
                return true;

        }

        return false;

    }


}
