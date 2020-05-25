package com.upload.app.modular.system.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.upload.app.modular.system.dao.AccessMapper;
import com.upload.app.modular.system.model.Access;
import com.upload.app.modular.system.service.AccessService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AccessServiceImpl extends ServiceImpl<AccessMapper, Access> implements AccessService {

    @Resource
    private AccessMapper accessMapper;

    @Override
    public Access findByAccessKey(String accesskey) {
        return accessMapper.findByAccessKey(accesskey);
    }

}
