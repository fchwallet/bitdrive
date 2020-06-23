package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.dao.AddressScriptLinkMapper;
import com.upload.app.modular.system.model.AddressScriptLink;
import com.upload.app.modular.system.service.AddressScriptLinkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressScriptLinkServiceImpl implements AddressScriptLinkService {

    @Resource
    private AddressScriptLinkMapper addressScriptLinkMapper;

    @Override
    public int insert(AddressScriptLink addressScriptLink) {
        return addressScriptLinkMapper.insert(addressScriptLink);
    }

    @Override
    public int findCount(String address, String script) {
        return addressScriptLinkMapper.findCount(address, script);
    }

    @Override
    public List<String> findListByAddress(String address) {
        return addressScriptLinkMapper.findListByAddress(address);
    }

    @Override
    public List<String> findByScript(String script) {
        return addressScriptLinkMapper.findByScript(script);
    }

}
