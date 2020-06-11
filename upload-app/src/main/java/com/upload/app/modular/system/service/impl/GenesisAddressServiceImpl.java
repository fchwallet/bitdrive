package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.dao.GenesisAddressMapper;
import com.upload.app.modular.system.model.GenesisAddress;
import com.upload.app.modular.system.service.GenesisAddressService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GenesisAddressServiceImpl implements GenesisAddressService {

    @Resource
    private GenesisAddressMapper genesisAddressMapper;


    @Override
    public int insertGenesisAddress(GenesisAddress genesisAddress) {
        return genesisAddressMapper.insertGenesisAddress(genesisAddress);
    }

    @Override
    public GenesisAddress findByTxidAndRaiseVout(String txid, Integer raiseVout) {
        return genesisAddressMapper.findByTxidAndRaiseVout(txid, raiseVout);
    }

    @Override
    public int updateGensisAddress(GenesisAddress genesisAddress) {
        return genesisAddressMapper.updateGensisAddress(genesisAddress);
    }

    @Override
    public GenesisAddress findRaiseAddress(String raiseAddress, String tokenId) {
        return genesisAddressMapper.findRaiseAddress(raiseAddress, tokenId);
    }

    @Override
    public GenesisAddress findByRaiseTxidAndRaiseVout(String raiseTxid, Integer raiseVout) {
        return genesisAddressMapper.findByRaiseTxidAndRaiseVout(raiseTxid, raiseVout);
    }

}
