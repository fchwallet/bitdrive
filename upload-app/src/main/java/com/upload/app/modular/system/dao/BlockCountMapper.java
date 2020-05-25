package com.upload.app.modular.system.dao;

import org.apache.ibatis.annotations.Param;

public interface BlockCountMapper {

    int updateBlock(@Param("height") Integer height);

    int findBlockCount();

}
