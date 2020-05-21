package com.xyz.upload.app.modular.system.dao;

import com.xyz.upload.app.modular.system.model.AddressDriveLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AddressDriveLinkMapper {

    int insert(AddressDriveLink addressDriveLinke);

    List<AddressDriveLink> findByAddress(String address);

    AddressDriveLink findByAddressAndDriveId(@Param("address") String address, @Param("driveId") String driveId);

}
