package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.ScriptSlpMint;
import org.apache.ibatis.annotations.Param;

public interface ScriptSlpMintMapper {

   int insertSlpMint(ScriptSlpMint scriptSlpMint);

   ScriptSlpMint findByToken(@Param("tokenId") String tokenId, @Param("address") String address);

}
