package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.AddressSignHash;
import org.apache.ibatis.annotations.Param;

public interface AddressSignHashMapper {

    int insert(AddressSignHash addressSignHash);

    AddressSignHash findByAddressAndHash(@Param("address") String address, @Param("hash") String hash);

}
