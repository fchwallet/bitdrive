package com.xyz.upload.app.modular.system.service;

import com.xyz.upload.app.modular.system.model.AddressDriveLink;

import java.util.List;

public interface AddressDriveLinkService {

    int insert(AddressDriveLink addressDriveLinke);

    List<AddressDriveLink> findByAddress(String address);

    AddressDriveLink findByAddressAndDriveId(String address, String driveId);

}
