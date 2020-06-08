package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.AddressDriveLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AddressDriveLinkMapper {

    int insert(AddressDriveLink addressDriveLinke);

    List<AddressDriveLink> findByAddress(@Param("address") String address, @Param("status") Integer status);

    List<AddressDriveLink> findDriveByAddress(String address);

    AddressDriveLink findByAddressAndDriveId(@Param("address") String address, @Param("driveId") String driveId);

}
