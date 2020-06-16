package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.BalanceHistory;

import java.util.List;

public interface BalanceHistoryMapper {

    int insert(BalanceHistory balanceHistory);

    List<BalanceHistory> findByAddress(String address);

}
