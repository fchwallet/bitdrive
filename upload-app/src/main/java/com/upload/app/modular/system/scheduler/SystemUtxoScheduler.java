package com.upload.app.modular.system.scheduler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.upload.app.core.common.annotion.TimeStat;
import com.upload.app.core.util.HttpUtil;
import com.upload.app.modular.system.model.SystemUtxo;
import com.upload.app.modular.system.service.BlockingQueueService;
import com.upload.app.modular.system.service.SystemUtxoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@ConditionalOnProperty(prefix = "guns.scheduler-switch", name = "systemutxoture", havingValue = "true")
@Slf4j
public class SystemUtxoScheduler {

    @Autowired
    private SystemUtxoScheduler self;

    @Autowired
    private BlockingQueueService blockingQueueService;

    @Autowired
    private SystemUtxoService systemUtxoService;

    final String sysAddress = "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx";

//    @Scheduled(cron = "0 */1 * * * ?")
    public void work() throws Exception {
        self.start();
    }

    @TimeStat
    public void start() {

        Map query = new HashedMap();
        query.put("address", "1D6swyzdkonsw6cBwFsFqNiT1TeJk7iqmx");
        JSONObject utxo = (JSONObject) JSONObject.parse(HttpUtil.doPost("http://47.110.137.123:8433/rest/Api/getUtxo",query));
        JSONObject udata = utxo.getJSONObject("data");
        JSONArray utxos = udata.getJSONArray("utxo");
        List<SystemUtxo> findUtxoList = new ArrayList<>();


        List<SystemUtxo> systemUtxoList = systemUtxoService.findByAddress(sysAddress);

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
        }

        List<SystemUtxo> dList = findUtxoList.stream().filter(sys -> !systemUtxoList.stream().map(d -> d.getTxid()).
                collect(Collectors.toList()).contains(sys.getTxid())).collect(Collectors.toList());

        for (SystemUtxo SystemUtxo : dList ) {

            systemUtxoService.insert(SystemUtxo);
            blockingQueueService.addQueue(SystemUtxo);

        }





//        for (Object ob : utxos) {
//
//            JSONObject ux = (JSONObject) ob;
//            String txid = ux.getString("txid");
//            Integer n = ux.getInteger("n");
//            SystemUtxo su = new SystemUtxo();
//            su.setTxid(txid);
//            su.setN(n);
//            su.setValue(ux.getString("value"));
//            su.setAddress(ux.getString("address"));
//            findUtxoList.add(su);
//
//            if (!systemUtxoService.isExistence(txid, n)) {
//
//                String address = ux.getString("address");
//                String value = ux.getString("value");
//                SystemUtxo systemUtxo = new SystemUtxo();
//                systemUtxo.setAddress(address);
//                systemUtxo.setN(n);
//                systemUtxo.setTxid(txid);
//                systemUtxo.setValue(value);
//                systemUtxoService.insert(systemUtxo);
//                blockingQueueService.addQueue(systemUtxo);
//            }
//
//        }

//        List<SystemUtxo> systemUtxoList = systemUtxoService.findByAddress(sysAddress);

//        List<SystemUtxo> dList = systemUtxoList.stream().filter(sys -> !findUtxoList.contains(sys)).collect(toList());

//        List<SystemUtxo> dList = systemUtxoList.stream().filter(sys -> !findUtxoList.stream().map(d -> d.getTxid()).
//                collect(Collectors.toList()).contains(sys.getTxid())).collect(Collectors.toList());
//
//        for (SystemUtxo SystemUtxo : dList ) {
//
//            systemUtxoService.delete(SystemUtxo.getTxid(),SystemUtxo.getN());
//
//        }



    }



}

