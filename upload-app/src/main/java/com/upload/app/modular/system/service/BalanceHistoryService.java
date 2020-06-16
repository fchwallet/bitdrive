package com.upload.app.modular.system.service;

import com.upload.app.modular.system.model.BalanceHistory;

import java.util.List;

public interface BalanceHistoryService {

    int insert(BalanceHistory balanceHistory);

    List<BalanceHistory> findByAddress(String address);


}
