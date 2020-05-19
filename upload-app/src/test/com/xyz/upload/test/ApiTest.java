package com.xyz.upload.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xyz.upload.app.core.rpc.Api;
import com.xyz.upload.app.core.rpc.CommonTxOputDto;
import com.xyz.upload.app.core.rpc.TxInputDto;
import com.xyz.upload.app.core.util.Sha256;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ApiTest extends ApplicationTest {


    @Test
    public void create() throws Exception {

        List<TxInputDto> inputs = new ArrayList<>();
        List<CommonTxOputDto> outputs = new ArrayList<>();

        TxInputDto input = new TxInputDto("fed9e0c835372abbae27806cbafbaac99a210fda41a16478cf6361b328a399d2",1,"");
        inputs.add(input);

        String[] address = {"12iQSzRVvmF8gjZhqKdoNBm5rJqDqn87y4", "1KrQoqUXY93cSAegJqSm2zSTuM3CQ64unX"};
        String metadata = "0446454950010201030643524541544532be53e7644ea9c8fed3ff8bda7ac87742a2e1494ac4e777ae396f388bf6e75f49010000";
        CommonTxOputDto c1 = new CommonTxOputDto(address, new BigDecimal("0.0001"), metadata, 1);
        outputs.add(c1);
        String data = "01354534564654564564564654564654";
        CommonTxOputDto c2 = new CommonTxOputDto(data, 3);
        outputs.add(c2);

        String createHex = Api.CreateDrivetx(inputs, outputs);
        String signHex = Api.SignDrivetx(createHex, "1KrQoqUXY93cSAegJqSm2zSTuM3CQ64unX");
        String a = Api.SendRawTransaction(signHex);

        JSONObject j = Api.GetRawTransaction(a);

        JSONArray vouts = j.getJSONArray("vout");
        String b = "";

        for (Object v : vouts) {
            JSONObject vout = (JSONObject) v;
            JSONObject scriptPubKey = vout.getJSONObject("scriptPubKey");
            String hex = scriptPubKey.getString("hex");
            b = Sha256.getSHA256(hex);
            break;
        }

        System.out.println(b);

    }

    @Test
    public void update() throws Exception {

        List<TxInputDto> inputs = new ArrayList<>();
        List<CommonTxOputDto> outputs = new ArrayList<>();

        TxInputDto input = new TxInputDto("fed9e0c835372abbae27806cbafbaac99a210fda41a16478cf6361b328a399d2",1,"");
        inputs.add(input);

        String[] address = {"12iQSzRVvmF8gjZhqKdoNBm5rJqDqn87y4", "1KrQoqUXY93cSAegJqSm2zSTuM3CQ64unX"};
        String metadata = "04464549500102010306555044415445321e6dc8d860dd8a54ce8e6bfe15f52d05a9e594dc75c112156354bd6b461e340932bcf17df18475759c21ee708f87130004e31bf071b3a8e002353a13664143f7cb";
        CommonTxOputDto c1 = new CommonTxOputDto(address, new BigDecimal("0.0001"), metadata, 1);
        outputs.add(c1);
        String data = "01354534564654564564564654564654";
        CommonTxOputDto c2 = new CommonTxOputDto(data, 3);
        outputs.add(c2);

        String createHex = Api.CreateDrivetx(inputs, outputs);
        String signHex = Api.SignDrivetx(createHex, "1KrQoqUXY93cSAegJqSm2zSTuM3CQ64unX");
        String a = Api.SendRawTransaction(signHex);

        JSONObject j = Api.GetRawTransaction(a);

        JSONArray vouts = j.getJSONArray("vout");
        String b = "";

        for (Object v : vouts) {
            JSONObject vout = (JSONObject) v;
            JSONObject scriptPubKey = vout.getJSONObject("scriptPubKey");
            String hex = scriptPubKey.getString("hex");
            b = Sha256.getSHA256(hex);
            break;
        }

        System.out.println(b);

    }



}


