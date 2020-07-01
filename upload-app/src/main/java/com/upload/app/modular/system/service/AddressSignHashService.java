package com.upload.app.modular.system.service;

import com.upload.app.modular.system.model.AddressSignHash;

public interface AddressSignHashService {

    int insert(AddressSignHash addressSignHash);

    AddressSignHash findByAddressAndHash(String address, String hash);

}
