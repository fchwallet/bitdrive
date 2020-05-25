package com.upload.app.modular.system.service.impl;

import com.upload.app.modular.system.service.BlockCountService;
import com.upload.app.modular.system.dao.BlockCountMapper;
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
