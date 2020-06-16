package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.dao.BalanceHistoryMapper;
import com.upload.app.modular.system.model.BalanceHistory;
import com.upload.app.modular.system.service.BalanceHistoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BalanceHistoryServiceImpl implements BalanceHistoryService {

    @Resource
    private BalanceHistoryMapper balanceHistoryMapper;

    @Override
    public int insert(BalanceHistory balanceHistory) {
        return balanceHistoryMapper.insert(balanceHistory);
    }

    @Override
    public List<BalanceHistory> findByAddress(String address) {
        return balanceHistoryMapper.findByAddress(address);
    }

}
