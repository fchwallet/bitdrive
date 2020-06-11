package com.upload.app.modular.system.dao;

import com.upload.app.modular.system.model.AddressScriptLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AddressScriptLinkMapper {

    int insert(AddressScriptLink addressScriptLink);

    int findCount(@Param("address") String address, @Param("script") String script);

    List<String> findListByAddress(String address);

}
