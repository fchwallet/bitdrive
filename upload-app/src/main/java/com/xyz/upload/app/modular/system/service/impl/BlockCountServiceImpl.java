package com.xyz.upload.app.modular.system.service.impl;

import com.xyz.upload.app.modular.system.dao.BlockCountMapper;
import com.xyz.upload.app.modular.system.service.BlockCountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BlockCountServiceImpl implements BlockCountService {

    @Resource
    private BlockCountMapper blockCountMapper;

    @Override
    public int updateBlock(Integer height) {
        return blockCountMapper.updateBlock(height);
    }

    @Override
    public int findBlockCount() {
        return blockCountMapper.findBlockCount();
    }


}
