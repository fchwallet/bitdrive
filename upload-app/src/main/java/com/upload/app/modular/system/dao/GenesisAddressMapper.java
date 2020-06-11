package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.GenesisAddress;
import org.apache.ibatis.annotations.Param;

public interface GenesisAddressMapper {

    int insertGenesisAddress(GenesisAddress genesisAddress);

    GenesisAddress findByTxidAndRaiseVout(@Param("txid") String txid, @Param("raiseVout") Integer raiseVout);

    int updateGensisAddress(GenesisAddress genesisAddress);

    GenesisAddress findRaiseAddress(@Param("raiseAddress") String raiseAddress, @Param("tokenId") String tokenId);

    GenesisAddress findByRaiseTxidAndRaiseVout(@Param("raiseTxid") String raiseTxid, @Param("raiseVout") Integer raiseVout);

}
