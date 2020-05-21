package com.xyz.upload.app.modular.system.service.impl;

import com.xyz.upload.app.modular.system.dao.AddressDriveLinkMapper;
import com.xyz.upload.app.modular.system.model.AddressDriveLink;
import com.xyz.upload.app.modular.system.service.AddressDriveLinkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressDriveLinkServiceImpl implements AddressDriveLinkService {

    @Resource
    private AddressDriveLinkMapper addressDriveLinkMapper;


    @Override
    public int insert(AddressDriveLink addressDriveLinke) {
        return addressDriveLinkMapper.insert(addressDriveLinke);
    }

    @Override
    public List<AddressDriveLink> findByAddress(String address) {
        return addressDriveLinkMapper.findByAddress(address);
    }

    @Override
    public AddressDriveLink findByAddressAndDriveId(String address, String driveId) {
        return addressDriveLinkMapper.findByAddressAndDriveId(address, driveId);
    }

}
