package com.upload.app.modular.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.upload.app.core.rpc.Api;
import com.upload.app.core.util.UnicodeUtil;
import com.upload.app.modular.system.model.*;
import com.upload.app.modular.system.service.*;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;

@Service
public class TokenDecodeServiceImpl implements TokenDecodeService {

    @Resource
    private ScriptTokenLinkService scriptTokenLinkService;

    @Resource
    private ScriptUtxoTokenLinkService scriptUtxoTokenLinkService;

    @Resource
    private AddressScriptLinkService addressScriptLinkService;

    @Resource
    private GenesisAddressService genesisAddressService;

    @Resource
    private ScriptSlpMintService scriptSlpMintService;

    @Resource
    private ScriptSlpService scriptSlpService;

    @Override
    public Map<String, Object> decodeToken(String txid, JSONArray vins, JSONArray vouts, JSONObject vout, String content, JSONObject scriptPubKey, Integer n, StringBuffer scrpit, List<String> addressList) {

        List<ScriptTokenLink> scriptTokenLinkList = scriptTokenLinkService.selectByTxid(txid);

        if (scriptTokenLinkList != null && scriptTokenLinkList.size() > 0)
            return null;

        Map<Integer, String> map = vouts(vouts);
        Boolean sendFlag = false;
        boolean flag = false;           // 销毁立flag, 如果最后是false并且当前的vin包含token，则销毁

        Map<String, Object> result = decode(scriptPubKey, vout, n, txid, map, vins,
               flag, content, scrpit.toString(), addressList);

        return result;

//        if (f == 1) {
//            return null;
//        } else if (f == 2) {
//            return null;
//        } else if (f == 3) {
//            result.put("flag", false);
//            return result;
//        } else if (f == 4) {
//            result.put("flag", true);
//            return result;
//        } else {
//            return null;
//        }

    }

    public Map<Integer, String> vouts(JSONArray vouts){

        Map<Integer, String> map = new HashedMap();

        for (Object v : vouts) {

            JSONObject vout = (JSONObject) v;
            JSONObject scriptPubKey = vout.getJSONObject("scriptPubKey");
            Integer n = vout.getInteger("n");
            String xsvaddressHash = scriptPubKey.getString("hex");
            map.put(n, xsvaddressHash);

        }

        return map;

    }



    public Map<String, Object> decode(JSONObject scriptPubKey, JSONObject vout, Integer n, String txid, Map map, JSONArray vins, Boolean flag,
                                      String OP_RETURN, String hexStr, List<String> addressList) {

        Map<String, Object> result = new HashedMap();

        String slpp = OP_RETURN.substring(0, 20);

        if (!"06534c502b2b00020201".equals(slpp)) {
            return null;  // continue
        }

        OP_RETURN = OP_RETURN.replaceFirst("06534c502b2b00020201", "");

        if ("".equals(OP_RETURN))
            return null;  // continue

        String content = OP_RETURN.substring(0, 2);


        Integer token_type = UnicodeUtil.decodeHEX(content);
        OP_RETURN = OP_RETURN.replaceFirst(content, "");

        if (token_type * 2 > OP_RETURN.length())
            return null;  // continue

        String token_type_str = OP_RETURN.substring(0, token_type * 2);
        OP_RETURN = OP_RETURN.replaceFirst(token_type_str, "");

        if ("47454e45534953".equals(token_type_str)) {

            String tokenid = "";

            try {
                String nn = UnicodeUtil.intToHex(n);
                tokenid = UnicodeUtil.getSHA256(txid + nn);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String vouthex = scriptPubKey.getString("hex");
            String value = vout.getBigDecimal("value").toString();

            boolean bl = false;

            try {
                bl = decodeGenesistoken(OP_RETURN, map, hexStr, tokenid, txid, n, vouthex, value, addressList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bl)
                flag = true;
            else {
                flag = false;
                return null;  // break
            }

        } else if ("4d494e54".equals(token_type_str)) {

            String vouthex = scriptPubKey.getString("hex");
            String value = vout.getBigDecimal("value").toString();
            boolean bl = false;

            try {
                bl = decodeMinttoken(vins, OP_RETURN, map, hexStr, txid, n, vouthex, value, addressList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bl)
                flag = true;
            else {
                flag = false;
                return null;  // break
            }

        } else if ("53454e44".equals(token_type_str)) {

            String vouthex = scriptPubKey.getString("hex");
            String value = vout.getBigDecimal("value").toString();

            Map<String, Object> f = decodeSnedToken(OP_RETURN, hexStr, n, vins, txid, vouthex, value, addressList);

            return f;

//            if (f || sendFlag)
//                sendFlag = true;
//
//            if (sendFlag)
//                flag = false;
//            else
//                flag = true;

        }

        if (flag) {
            result.put("flag", false);
            return result;
        } else {
            result.put("flag", true);
            return result;
        }

    }

    //解析发行
    @Transactional(rollbackFor=Exception.class)
    public boolean decodeGenesistoken(String content, Map<Integer, String> map, String hexStr, String tokenId, String txid, Integer n, String vouthex, String value, List<String> addressList) {

        try {

            String token_ticker_hex = content.substring(0, 2);
            Integer token_ticker = UnicodeUtil.decodeHEX(token_ticker_hex);
            content = content.replaceFirst(token_ticker_hex, "");
            String token_ticker_str = content.substring(0, token_ticker * 2);

            content = content.replaceFirst(token_ticker_str, "");				// 清空掉token_ticker
            String tokenTicker = UnicodeUtil.hexStringToString(token_ticker_str);

            String token_name_hex = content.substring(0, 2);
            Integer token_name = UnicodeUtil.decodeHEX(token_name_hex);
            content = content.replaceFirst(token_name_hex, "");
            String token_name_str = content.substring(0, token_name * 2);

            content = content.replaceFirst(token_name_str, "");				// 清空掉token_name
            String tokenName = UnicodeUtil.hexStringToString(token_name_str);

            String token_document_url_hex = content.substring(0, 2);
            Integer token_document_url = UnicodeUtil.decodeHEX(token_document_url_hex);
            content = content.replaceFirst(token_document_url_hex, "");
            String token_document_url_str = content.substring(0, token_document_url * 2);

            content = content.replaceFirst(token_document_url_str, "");		// 清空掉url
            String tokenUrl = UnicodeUtil.hexStringToString(token_document_url_str);

            String token_document_hash_hex = content.substring(0, 2);
            Integer token_document_hash = UnicodeUtil.decodeHEX(token_document_hash_hex);
            content = content.replaceFirst(token_document_hash_hex, "");
            String token_document_hash_str = content.substring(0, token_document_hash * 2);

            content = content.replaceFirst(token_document_hash_str, "");		// 清空掉hash


            String byte_length_hex = content.substring(0, 2);
            Integer byte_length = UnicodeUtil.decodeHEX(byte_length_hex);
            content = content.replaceFirst(byte_length_hex, "");
            String byte_length_str = content.substring(0, byte_length * 2);

            if (byte_length < 0 || byte_length > 9) {
                // 超出范围
                return false;
            }
            content = content.replaceFirst(byte_length_str, "");			    // 清空掉精度precition
            Integer precition = new BigInteger(byte_length_str, 16).intValue();

            String mint_baton_vout_hex = content.substring(0, 2);
            Integer mint_baton_vout = UnicodeUtil.decodeHEX(mint_baton_vout_hex);
            content = content.replaceFirst(mint_baton_vout_hex, "");
            String mint_baton_vout_str = content.substring(0, mint_baton_vout * 2);

            if (mint_baton_vout < 1 || mint_baton_vout > 255) {
                // 超出范围
                return false;
            }
            content = content.replaceFirst(mint_baton_vout_str, "");			//清空掉vout
            Integer mintVout = new BigInteger(mint_baton_vout_str, 16).intValue();

            String initial_token_mint_quantity_hex = content.substring(0, 2);
            Integer initial_token_mint_quantity = UnicodeUtil.decodeHEX(initial_token_mint_quantity_hex);
            content = content.replaceFirst(initial_token_mint_quantity_hex, "");
            String initial_token_mint_quantity_str = content.substring(0, initial_token_mint_quantity * 2);
            BigInteger quantity = new BigInteger(initial_token_mint_quantity_str, 16);

            if (quantity.compareTo(new BigInteger("0")) < 0|| quantity.compareTo(new BigInteger("18446744073709551999")) > 0) {
                // 超出范围
                return false;
            }

            content = content.replaceFirst(initial_token_mint_quantity_str, "");				// 清空掉mintQuantity
            if (!"".equals(content)) {
                // 格式错误
                return false;
            }


            String mintAddress = map.get(mintVout).replaceFirst("76a914", "").replaceFirst("88ac", "");   //增发权限地址
            ScriptSlp scriptSlp = new ScriptSlp();
            scriptSlp.setTokenTicker(tokenTicker);
            scriptSlp.setTokenName(tokenName);
            scriptSlp.setTokenDocumentUrl(tokenUrl);
            scriptSlp.setTokenDocumentHash(token_document_hash_str);
            scriptSlp.setTokenDecimal(precition);
            scriptSlp.setMintBatonVout(mintVout);
            scriptSlp.setTransactionType("GENESIS");
            scriptSlp.setOriginalScrpit(hexStr);                                // 发行的脚本
            scriptSlp.setInitIssueAddress(mintAddress);                // 增发权限地址
            scriptSlp.setInitialTokenMintQuantity(quantity.toString());
            scriptSlp.setTxid(tokenId);
            scriptSlpService.insertSlp(scriptSlp);

            GenesisAddress genesisAddress = new GenesisAddress();
            genesisAddress.setTxid(tokenId);
            genesisAddress.setRaiseVout(mintVout);
            genesisAddress.setIssueAddress(hexStr);
            genesisAddress.setRaiseAddress(mintAddress);
            genesisAddress.setIssueVout(n);
            genesisAddress.setRaiseTxid(txid);
            genesisAddressService.insertGenesisAddress(genesisAddress);


            ScriptTokenLink tokenAssets = new ScriptTokenLink();
            tokenAssets.setScript(hexStr);
            tokenAssets.setTokenId(tokenId);
            tokenAssets.setTxid(txid);
            tokenAssets.setVout(n);
            tokenAssets.setToken(quantity);
            tokenAssets.setStatus(0);
            scriptTokenLinkService.insert(tokenAssets);

            for (String address: addressList) {

                ScriptUtxoTokenLink UtxoToken = new ScriptUtxoTokenLink();
                UtxoToken.setAddress(address);
                UtxoToken.setN(n);
                UtxoToken.setScript(vouthex);
                UtxoToken.setTxid(txid);
                UtxoToken.setValue(value);
                UtxoToken.setScript(hexStr);
                scriptUtxoTokenLinkService.insert(UtxoToken);

                int count = addressScriptLinkService.findCount(address, hexStr);
                if (count < 1) {
                    AddressScriptLink asl = new AddressScriptLink();
                    asl.setAddress(address);
                    asl.setScript(hexStr);
                    addressScriptLinkService.insert(asl);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return true;

    }


    // 解析增发
    @Transactional(rollbackFor=Exception.class)
    public boolean decodeMinttoken(JSONArray vins, String content, Map<Integer, String> map, String hexStr, String tx, Integer n, String vouthex, String value, List<String> addressList) {

        try {

            String token_id_hex = content.substring(0, 2);
            Integer token_id = UnicodeUtil.decodeHEX(token_id_hex);

            content = content.replaceFirst(token_id_hex, "");
            String token_id_str = content.substring(0, token_id * 2);				//token_id

            content = content.replaceFirst(token_id_str, "");				// 清空掉token_id

            String mint_baton_vout_hex = content.substring(0, 2);
            Integer mint_baton_vout = UnicodeUtil.decodeHEX(mint_baton_vout_hex);
            content = content.replaceFirst(mint_baton_vout_hex, "");
            String mint_baton_vout_str = content.substring(0, mint_baton_vout * 2);		// vout

            if (mint_baton_vout < 1 || mint_baton_vout > 255) {
                // 超出范围
                return false;
            }
            content = content.replaceFirst(mint_baton_vout_str, "");			//清空掉vout

            Integer mintVout = new BigInteger(mint_baton_vout_str, 16).intValue();

            String initial_token_mint_quantity_hex = content.substring(0, 2);
            Integer initial_token_mint_quantity = UnicodeUtil.decodeHEX(initial_token_mint_quantity_hex);
            content = content.replaceFirst(initial_token_mint_quantity_hex, "");
            String initial_token_mint_quantity_str = content.substring(0, initial_token_mint_quantity * 2);
            BigInteger quantity = new BigInteger(initial_token_mint_quantity_str, 16);

            if (quantity.compareTo(new BigInteger("0")) < 0|| quantity.compareTo(new BigInteger("18446744073709551999")) > 0) {
                // 超出范围
                return false;
            }

            content = content.replaceFirst(initial_token_mint_quantity_str, "");				// 清空掉mintQuantity
            if (!"".equals(content)) {
                // 格式错误
                return false;
            }

            String mintAddress = map.get(mintVout).replaceFirst("76a914","").replaceFirst("88ac","");   //增发权限地址


            ScriptSlp scriptSlp = scriptSlpService.findByTokenId(token_id_str);
            if (scriptSlp == null) {
                return false;   // 不存在token
            }

            boolean f = false;

            try {
                f = mintVins(vins, token_id_str);         // 判断有没有增发权限
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!f) {
                return false;
            }

            ScriptSlpMint slpMint = new ScriptSlpMint();
            slpMint.setTransactionType("mint");
            slpMint.setTokenId(token_id_str);
            slpMint.setMintBatonVout(mintVout);
            slpMint.setAdditionalTokenQuantity(quantity.toString());
            slpMint.setScript(hexStr);         //增发脚本
            slpMint.setMinterAddress(mintAddress);

            scriptSlpMintService.insertSlpMint(slpMint);

            GenesisAddress genesisAddress = new GenesisAddress();
            genesisAddress.setRaiseAddress(mintAddress);
            genesisAddress.setTxid(token_id_str);
            genesisAddress.setRaiseVout(mintVout);
            genesisAddress.setRaiseTxid(tx);
            genesisAddressService.updateGensisAddress(genesisAddress);

            ScriptTokenLink tokenAssets = new ScriptTokenLink();
            tokenAssets.setScript(hexStr);
            tokenAssets.setTokenId(token_id_str);
            tokenAssets.setTxid(tx);
            tokenAssets.setVout(n);
            tokenAssets.setToken(quantity);
            tokenAssets.setStatus(1);
            scriptTokenLinkService.insert(tokenAssets);

            for (String address: addressList) {

                ScriptUtxoTokenLink UtxoToken = new ScriptUtxoTokenLink();
                UtxoToken.setAddress(address);
                UtxoToken.setN(n);
                UtxoToken.setScript(vouthex);
                UtxoToken.setTxid(tx);
                UtxoToken.setValue(value);
                UtxoToken.setScript(hexStr);
                scriptUtxoTokenLinkService.insert(UtxoToken);

                int count = addressScriptLinkService.findCount(address, hexStr);
                if (count < 1) {
                    AddressScriptLink asl = new AddressScriptLink();
                    asl.setAddress(address);
                    asl.setScript(hexStr);
                    addressScriptLinkService.insert(asl);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return true;

    }

    //解析发送
    public Map<String, Object> decodeSnedToken(String content, String toAddressHash, Integer n, JSONArray vins, String tx,
                                    String vouthex, String value,  List<String> addressList) {

        Map<String, Object> result = new HashedMap();
        List<ScriptSlpSend> SlpSendList = new ArrayList<>();
        List<ScriptTokenLink> TokenAssetsList = new ArrayList<>();
        List<ScriptUtxoTokenLink> UtxoTokenList = new ArrayList<>();
        List<AddressScriptLink> addressScriptLink = new ArrayList<>();

        String token_id_hex = content.substring(0, 2);
        Integer token_id = UnicodeUtil.decodeHEX(token_id_hex);				// 获取token
        content = content.replaceFirst(token_id_hex, "");
        String token_id_str = content.substring(0, token_id * 2);
        content = content.replaceFirst(token_id_str, "");

        String quantity_hex = content.substring(0, 2);

        Integer quantity = UnicodeUtil.decodeHEX(quantity_hex);

        content = content.replaceFirst(quantity_hex, "");
        String quantity_hex_str = content.substring(0, quantity * 2);

        BigInteger quantity_int = new BigInteger(quantity_hex_str, 16);

        if (quantity_int.compareTo(new BigInteger("0")) < 0|| quantity_int.compareTo(new BigInteger("18446744073709551999")) > 0) {
            // 超出范围
            return null;
        }

        List<ScriptTokenLink> assetsList = new ArrayList<>();


        for (Object v : vins) {
            JSONObject vin = (JSONObject) v;
            String txid = vin.getString("txid");
            Integer vout = vin.getInteger("vout");

            ScriptTokenLink ScriptAssets = scriptTokenLinkService.findByTokenAssets(token_id_str, txid, vout);

            if (ScriptAssets != null) {
                assetsList.add(ScriptAssets);
            }

        }

        Set<String> fromAddress = new HashSet<>();
        BigInteger newBig = new BigInteger("0");

        if (assetsList != null && assetsList.size() > 0) {

            for (ScriptTokenLink tokenAssets : assetsList) {

                BigInteger fromToken = scriptTokenLinkService.selectFAToken(token_id_str, tokenAssets.getTxid(), tokenAssets.getVout());                 // 查询脚本的
                fromAddress.add(tokenAssets.getScript());
                if (fromToken != null) {
                    newBig = newBig.add(fromToken);
                }
            }

        } else {
            result.put("flag", false);
            return result;
        }


        if (newBig.compareTo(quantity_int) < 0) {
            result.put("flag", false);
            return result;             //钱不够，返回
        }

        ScriptSlpSend scriptSlpSend = new ScriptSlpSend();
        scriptSlpSend.setTokenId(token_id_str);
        scriptSlpSend.setTokenOutputQuantity(quantity.toString());
        scriptSlpSend.setVout(n);
        scriptSlpSend.setScript(toAddressHash);                  // 向这个脚本打钱
        scriptSlpSend.setTxid(tx);
        SlpSendList.add(scriptSlpSend);

        ScriptTokenLink scriptTokenLink = new ScriptTokenLink();

        if (fromAddress.size() > 1) {

            scriptTokenLink.setStatus(5);
            scriptTokenLink.setScript(toAddressHash);
            scriptTokenLink.setTokenId(token_id_str);
            scriptTokenLink.setTxid(tx);
            scriptTokenLink.setVout(n);
            scriptTokenLink.setToken(quantity_int);
            TokenAssetsList.add(scriptTokenLink);

        } else{

            String faddress = fromAddress.iterator().next();
            if (faddress.equals(toAddressHash)) {                    // 脚本相同，给自己找零
                scriptTokenLink.setStatus(4);
            } else {
                scriptTokenLink.setStatus(2);
            }
            scriptTokenLink.setScript(toAddressHash);
            scriptTokenLink.setTokenId(token_id_str);
            scriptTokenLink.setTxid(tx);
            scriptTokenLink.setVout(n);
            scriptTokenLink.setToken(quantity_int);
            scriptTokenLink.setFromScript(faddress);
            TokenAssetsList.add(scriptTokenLink);

        }

        for (String address: addressList) {
            ScriptUtxoTokenLink scriptUtxoTokenLink = new ScriptUtxoTokenLink();
            scriptUtxoTokenLink.setAddress(address);               // 地址
            scriptUtxoTokenLink.setN(n);
            scriptUtxoTokenLink.setScript(vouthex);
            scriptUtxoTokenLink.setTxid(tx);
            scriptUtxoTokenLink.setValue(value);
            scriptUtxoTokenLink.setScript(toAddressHash);         // 脚本
            UtxoTokenList.add(scriptUtxoTokenLink);
            int count = addressScriptLinkService.findCount(address, toAddressHash);
            if (count < 1) {
                AddressScriptLink asl = new AddressScriptLink();
                asl.setAddress(address);
                asl.setScript(toAddressHash);
                addressScriptLink.add(asl);
            }
        }

        result.put("SlpSendList", SlpSendList);
        result.put("TokenAssetsList", TokenAssetsList);
        result.put("UtxoTokenList", UtxoTokenList);
        result.put("addressScriptLink", addressScriptLink);
        result.put("sendFlag", true);
        result.put("flag", true);
        result.put("toAmount", quantity_int);
        result.put("fromAmount", newBig);

        return result;

    }

    public boolean mintVins(JSONArray vins, String tokenId) throws Exception {

        for (Object v: vins) {
            JSONObject vin = (JSONObject) v;
            JSONObject json = Api.GetRawTransaction(vin.getString("txid"));
            JSONArray vout = json.getJSONArray("vout");
            JSONObject vv = vout.getJSONObject(vin.getInteger("vout"));
            String xsvaddressHash = vv.getJSONObject("scriptPubKey").getString("hex").replaceFirst("76a914","").replaceFirst("88ac","");
            GenesisAddress genesisAddress = genesisAddressService.findRaiseAddress(xsvaddressHash, tokenId);
            if (genesisAddress != null)
                return true;
        }

        return false;
    }



}
