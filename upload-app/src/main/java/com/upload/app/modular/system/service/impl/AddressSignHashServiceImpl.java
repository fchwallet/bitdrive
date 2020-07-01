package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.dao.AddressSignHashMapper;
import com.upload.app.modular.system.model.AddressSignHash;
import com.upload.app.modular.system.service.AddressSignHashService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AddressSignHashServiceImpl implements AddressSignHashService {

    @Resource
    private AddressSignHashMapper addressSignHashMapper;

    @Override
    public int insert(AddressSignHash addressSignHash) {
        return addressSignHashMapper.insert(addressSignHash);
    }

    @Override
    public AddressSignHash findByAddressAndHash(String address, String hash) {
        return addressSignHashMapper.findByAddressAndHash(address, hash);
    }

}
