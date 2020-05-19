package com.xyz.upload.app.modular.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xyz.upload.app.core.rpc.Api;
import com.xyz.upload.app.core.util.Sha256;
import com.xyz.upload.app.core.util.UnicodeUtil;
import com.xyz.upload.app.modular.system.dao.CreateMapper;
import com.xyz.upload.app.modular.system.dao.FchXsvLinkMapper;
import com.xyz.upload.app.modular.system.model.Create;
import com.xyz.upload.app.modular.system.model.FchXsvLink;
import com.xyz.upload.app.modular.system.service.DecodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;


@Service
public class DecodeServiceImpl implements DecodeService {

    @Resource
    private CreateMapper createMapper;

    @Resource
    private FchXsvLinkMapper fchXsvLinkMapper;

    @Override
    public Boolean decodeCreate(JSONArray jsonArray) throws Exception {

        for (Object ob : jsonArray) {

            String tx = (String)ob;

            try {


                JSONObject txTransaction = Api.GetRawTransaction(tx);

                JSONArray vouts = txTransaction.getJSONArray("vout");

                for (Object v : vouts) {

                    JSONObject vout = (JSONObject)v;

                    BigDecimal value = vout.getBigDecimal("value");

                    JSONObject scriptPubKey = (JSONObject) vout.get("scriptPubKey");

                    String content = scriptPubKey.getString("hex");

                    Create create = new Create();
                    create.setDriveId(Sha256.getSHA256(content));


                    if (value.compareTo(new BigDecimal("0")) == 0) {

                        create.setData(content);

                    } else {

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

                        if (fchXsvLink1 != null)
                            create.setFchAddress(fchXsvLink1.getXsvAddress());

                        content = content.replaceFirst(address1, "");

                        String opa = content.substring(0, 4);

                        if ("88ac".equals(opa)) {
                            continue;
                        }

                        String if67 = content.substring(0, 2);

                        content = content.replaceFirst(if67, "");

                        if ("67".equals(if67)) {

                            String oph1 = content.substring(0, 6);

                            if (!"76a914".equals(oph1)) {
                                continue;
                            }

                            content = content.replaceFirst(oph1, "");
                            String address2 = content.substring(0, 40);

                            FchXsvLink fchXsvLink2 = fchXsvLinkMapper.findByHash(address2);

                            if (fchXsvLink2 != null)
                                create.setFchAddress(fchXsvLink2.getXsvAddress());

                            content = content.replaceFirst(address2, "");
                            if67 = content.substring(0, 2);
                            content = content.replaceFirst(if67, "");               //68

                        }

                        if (!"68".equals(if67)) {
                            continue;
                        }

                        String a6 = content.substring(0, 2);

                        if (a6.equals("6a"))
                            continue;

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

                        if ("CREATE".equals(action_str)) {

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

                                create.setEncryptedPwd(encrypted_pwd_str);

                            } else {
                                continue;
                            }
                            createMapper.insert(create);

                        } else if ("UPDATE".equals(action_str)) {

                            String data_hash = content.substring(0, 2);
                            content = content.replaceFirst(data_hash, "");
                            String data_hash_str = content.substring(0, Integer.valueOf(data_hash) * 2);
                            content = content.replaceFirst(data_hash_str, "");

                            String drive_id_hex = content.substring(0, 2);
                            Integer drive_id = UnicodeUtil.decodeHEX(drive_id_hex);
                            content = content.replaceFirst(drive_id_hex, "");
                            String drive_id_str = content.substring(0, drive_id * 2);
                            content = content.replaceFirst(drive_id_str, "");

                            Create createDrive = createMapper.findByDriveId(drive_id_str);

                            if (createDrive == null)
                                continue;


                        }

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }


        }

        return true;
    }


}
