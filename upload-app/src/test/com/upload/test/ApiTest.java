package com.upload.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.upload.app.core.rpc.Api;
import com.upload.app.core.rpc.CommonTxOputDto;
import com.upload.app.core.rpc.TxInputDto;
import com.upload.app.core.util.HttpUtil;
import com.upload.app.core.util.Sha256;
import org.junit.Test;

import java.io.*;
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

    @Test
    public void put() throws Exception {

        InputStream fi = new FileInputStream(new File("C:\\Users\\caiyile\\Desktop\\test\\test2.txt"));
        InputStreamReader fsr = new InputStreamReader(fi);
        BufferedReader br = new BufferedReader(fsr);
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine())!=null) {
            sb.append(line);
        }

        JSONObject j = new JSONObject();
        JSONArray jsr = new JSONArray();
        jsr.add("FAXr2MSU3HJrXyk4UMJKx5fX3KPX33sEiq");
        j.put("fch_addr", jsr);
        j.put("metadata", "464f43507c337c317c4352454154457c343335393566373637303431373637633434343534363439346534393534343934663465376365383837616165373934623165373865623065393837393137636538383761616537393462316537386562306539383739316536393861666539383039616538626638376539396439656535616662396537613762306535616638366537613038316536386138306536396361666535393238636537383262396535616662396537383262396537626439316537626239636535626262616537616238626535386562626534623861646535626638336535386339366537396138346538623461376535623838316537623362626537626239666566626338636535386462336534623838306537613738646535386562626534623861646535626638336535386339366537396138346535616638366537613038316538623461376535623838316533383038326538383761616537393462316537386562306539383739316537626261376536383962666534626138366536616639346537383962396535623838316535393238636536616639346537383962396535623838316537386562306539383739316537396138346535626139356535623138326536396562366536396538346566626338636535613239656535386161306534626138366536623262626537393038366535396662616539383739316535393238636536623262626537393038366536396362616535383862366566626338636536393838656537613161656534626138366536396538346535626262616535616638366537613038316537626238666536623538656537396138346536383062626534626439336537396261656536613038376533383038327c307c7c6e756c6c");
        j.put("data", sb.toString());
        j.put("signature", "HzaMGOWbp/nFjEEBAizKLh+IXV7PJzg1Q1+1XVoDkYaIK/edhcAicmHv0lje9lQVqTWErythCXums2jRH39/C0w=");
        String r = HttpUtil.doPost("http://116.62.126.223:8442/api/put", j.toJSONString());

        System.out.println(r);

    }

    @Test
    public void add() throws Exception {
        BigDecimal amount = new BigDecimal("0.1");

        for (int i = 0; i < 100; i++) {
            Api.SendToAddress("1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx", amount);
        }

    }

    @Test
    public void sgin() throws Exception {

        InputStream fi = new FileInputStream(new File("C:\\Users\\caiyile\\Desktop\\test\\test2.txt"));
        InputStreamReader fsr = new InputStreamReader(fi);
        BufferedReader br = new BufferedReader(fsr);
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine())!=null) {
            sb.append(line);
        }

        String a = Api.FchSignMessage("FAXr2MSU3HJrXyk4UMJKx5fX3KPX33sEiq", sb.toString());
        System.out.println(a);

    }

    @Test
    public void createGenniss() throws Exception {

        List<TxInputDto> inputs = new ArrayList<>();

        //0.0888504
        TxInputDto tx = new TxInputDto("9df873a5c4e7c4c0e40c21886c63675ee0207cef72590a6e01acb5bdb2160703", 2,"");
        inputs.add(tx);

        List<CommonTxOputDto> outputs = new ArrayList<>();
        String[] a = {"15hjZZ1PBy6Buos2cfeAyh8z1fNWBQVsZN","1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx"};
        CommonTxOputDto c1 = new CommonTxOputDto(a, new BigDecimal("0.0001"), "06534c502b2b000202010747454e45534953045553445423546574686572204c74642e20555320646f6c6c6172206261636b656420746f6b656e734168747470733a2f2f7465746865722e746f2f77702d636f6e74656e742f75706c6f6164732f323031362f30362f546574686572576869746550617065722e70646620db4451f11eda33950670aaf59e704da90117ff7057283b032cfaec77793139160108010108002386f26fc10000", 1);
        outputs.add(c1);
        String[] sysad = {"1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx"};
        CommonTxOputDto c2 = new CommonTxOputDto(sysad, new BigDecimal("0.0886504"), 2);
        outputs.add(c2);                                //找零

        String createHex = Api.CreateDrivetx(inputs, outputs);
        String signHex = Api.SignDrivetx(createHex, "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        String hex = Api.SendRawTransaction(signHex);
        System.out.println(hex);

    }

    @Test
    public void createMint() throws Exception {

        List<TxInputDto> inputs = new ArrayList<>();

        //0.0997205
        TxInputDto tx = new TxInputDto("3716adfca12f51a0afcf883b98da91c85eaf6516b48d699abd1ca88761a8cb6b", 1,"");
        inputs.add(tx);

        List<CommonTxOputDto> outputs = new ArrayList<>();
        String[] a = {"1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx","18x7ZqhUHV3NgCGAw3NPEsGbEZ5i6beyD6"};
        String[] my = {"1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx"};   // 找零
        CommonTxOputDto c1 = new CommonTxOputDto(a, new BigDecimal("0.0001"), "06534c502b2b000202010453454e44201a47f6d520fa0048d9de19bae99fa61e1d91f35f49dd6d1fc472926b70f51cb3080011c37937e08000", 1);
        outputs.add(c1);
        CommonTxOputDto c2 = new CommonTxOputDto(my, new BigDecimal("0.0995205"), 2);
        outputs.add(c2);

        String createHex = Api.CreateDrivetx(inputs, outputs);
        String signHex = Api.SignDrivetx(createHex, "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        String hex = Api.SendRawTransaction(signHex);
        System.out.println(hex);

    }


    @Test
    public void createSend() throws Exception {

        List<TxInputDto> inputs = new ArrayList<>();

        //0.0888995
        TxInputDto tx = new TxInputDto("a610048b834f990a4399748b95e5f656a90eefeb94022dce9ea14bdb0f5aa84e", 1,"");
        TxInputDto tx1 = new TxInputDto("4681ae58812a0db0d549e7e75457bd6b020f0267ef2515c93deb9b9f82f40e02", 1,"");
        inputs.add(tx);
        inputs.add(tx1);

        List<CommonTxOputDto> outputs = new ArrayList<>();
        String[] a = {"18x7ZqhUHV3NgCGAw3NPEsGbEZ5i6beyD6","1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx"};
//        String[] my = {"1P1KYzTrhVyjPe8HXzbavVwWSQ2rEgD4oi","1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx"};   // 找零
        CommonTxOputDto c1 = new CommonTxOputDto(a, new BigDecimal("0.0001"), "06534c502b2b000202010453454e44201a47f6d520fa0048d9de19bae99fa61e1d91f35f49dd6d1fc472926b70f51cb3080011c37937e08000", 1);
        outputs.add(c1);
//        CommonTxOputDto c11 = new CommonTxOputDto(my, new BigDecimal("0.0001"), "06534c502b2b000202010453454e44201a47f6d520fa0048d9de19bae99fa61e1d91f35f49dd6d1fc472926b70f51cb3080011c37937e08000", 1);
//        outputs.add(c11);         //找零
        String[] sysad = {"1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx"};
        CommonTxOputDto c2 = new CommonTxOputDto(sysad, new BigDecimal("0.0886995"), 2);
        outputs.add(c2);

        String createHex = Api.CreateDrivetx(inputs, outputs);
        String signHex = Api.SignDrivetx(createHex, "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        String hex = Api.SendRawTransaction(signHex);
        System.out.println(hex);

    }

}


