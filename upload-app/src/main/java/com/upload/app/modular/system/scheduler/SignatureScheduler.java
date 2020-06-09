package com.upload.app.modular.system.scheduler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.upload.app.core.common.annotion.TimeStat;
import com.upload.app.core.rpc.Api;
import com.upload.app.modular.system.service.BlockCountService;
import com.upload.app.modular.system.service.DecodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(prefix = "guns.scheduler-switch", name = "signature", havingValue = "true")
@Slf4j
public class SignatureScheduler {

    @Autowired
    private SignatureScheduler self;

    @Autowired
    private DecodeService dcodeService;

    @Autowired
    private BlockCountService blockCountService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void work() throws Exception {
        self.start();
    }

    @TimeStat
    @Transactional(rollbackFor=Exception.class)
    public void start() {

        try {

            Integer count = blockCountService.findBlockCount();

            Integer json = 0;

            try {

                json = Api.GetBlockCount();

            } catch (Exception e) {

                e.printStackTrace();

            }

            if (count < json + 1) {
                JSONObject block = new JSONObject();

                try {
                    String blockHash = Api.GetBlockHash(count);
                    block = Api.GetBlock(blockHash);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONArray jsonArray = block.getJSONArray("tx");

                try {
                    dcodeService.blockDecode(jsonArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                count ++;
                blockCountService.updateBlock(count);

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }



}


