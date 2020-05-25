package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.service.AddressDriveLinkService;
import com.upload.app.modular.system.dao.AddressDriveLinkMapper;
import com.upload.app.modular.system.model.AddressDriveLink;
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

    @Override
    public List<AddressDriveLink> findDriveByAddress(String address) {
        return addressDriveLinkMapper.findDriveByAddress(address);
    }

}
