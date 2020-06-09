package com.upload.app.modular.system.scheduler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.upload.app.core.common.annotion.TimeStat;
import com.upload.app.core.rpc.TxInputDto;
import com.upload.app.core.util.HttpUtil;
import com.upload.app.modular.system.dao.SystemUtxoMapper;
import com.upload.app.modular.system.model.SystemUtxo;
import com.upload.app.modular.system.service.SystemUtxoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "guns.scheduler-switch", name = "systemutxoture", havingValue = "true")
@Slf4j
public class SystemUtxoScheduler {

    @Autowired
    private SystemUtxoScheduler self;

    @Autowired
    private SystemUtxoService systemUtxoService;


    @Scheduled(cron = "0/15 * * * * ?")
    public void work() {
        self.start();
    }

    @TimeStat
    @Transactional(rollbackFor=Exception.class)
    public void start() {

        Map query = new HashedMap();
        query.put("address", "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        JSONObject utxo = (JSONObject) JSONObject.parse(HttpUtil.doPost("http://47.110.137.123:8433/rest/Api/getUtxo",query));
        JSONObject udata = utxo.getJSONObject("data");
        JSONArray utxos = udata.getJSONArray("utxo");

        for (Object ob : utxos) {

            JSONObject ux = (JSONObject) ob;
            String txid = ux.getString("txid");
            Integer n = ux.getInteger("n");

            if (!systemUtxoService.isExistence(txid, n)) {

                String address = ux.getString("address");
                String value = ux.getString("value");
                SystemUtxo systemUtxo = new SystemUtxo();
                systemUtxo.setAddress(address);
                systemUtxo.setN(n);
                systemUtxo.setTxid(txid);
                systemUtxo.setValue(value);
                systemUtxoService.insert(systemUtxo);

            }

        }

    }



}


