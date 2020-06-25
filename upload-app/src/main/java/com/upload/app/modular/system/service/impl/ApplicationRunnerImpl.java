package com.upload.app.modular.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.upload.app.core.util.HttpUtil;
import com.upload.app.modular.system.model.SystemUtxo;
import com.upload.app.modular.system.service.BlockingQueueService;
import com.upload.app.modular.system.service.SystemUtxoService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ApplicationRunnerImpl implements ApplicationRunner {

    @Resource
    private BlockingQueueService blockingQueueService;

    @Resource
    private SystemUtxoService systemUtxoService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String sysAddress = "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx";

        Map query = new HashedMap();
        query.put("address", "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        JSONObject utxo = (JSONObject) JSONObject.parse(HttpUtil.doPost("http://47.110.137.123:8433/rest/Api/getUtxo",query));
        JSONObject udata = utxo.getJSONObject("data");
        JSONArray utxos = udata.getJSONArray("utxo");
        List<SystemUtxo> findUtxoList = new ArrayList<>();

        for (Object ob : utxos) {

            JSONObject ux = (JSONObject) ob;
            String txid = ux.getString("txid");
            Integer n = ux.getInteger("n");
            SystemUtxo su = new SystemUtxo();
            su.setTxid(txid);
            su.setN(n);
            su.setValue(ux.getString("value"));
            su.setAddress(ux.getString("address"));
            findUtxoList.add(su);

            if (!systemUtxoService.isExistence(txid, n)) {

                String address = ux.getString("address");
                String value = ux.getString("value");
                SystemUtxo systemUtxo = new SystemUtxo();
                systemUtxo.setAddress(address);
                systemUtxo.setN(n);
                systemUtxo.setTxid(txid);
                systemUtxo.setValue(value);
                systemUtxoService.insert(systemUtxo);
                blockingQueueService.addQueue(systemUtxo);
            }

        }

        List<SystemUtxo> systemUtxoList = systemUtxoService.findByAddress(sysAddress);

        List<SystemUtxo> dList = systemUtxoList.stream().filter(sys -> !findUtxoList.stream().map(d -> d.getTxid()).
                collect(Collectors.toList()).contains(sys.getTxid())).collect(Collectors.toList());

        for (SystemUtxo SystemUtxo : dList ) {

            systemUtxoService.delete(SystemUtxo.getTxid(),SystemUtxo.getN());

        }

        List<SystemUtxo> list = systemUtxoService.findByAddress("1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");

        for (SystemUtxo su : list) {
            blockingQueueService.addQueue(su);
        }

    }

}
