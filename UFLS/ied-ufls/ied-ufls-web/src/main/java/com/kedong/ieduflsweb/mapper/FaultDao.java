package com.kedong.ieduflsweb.mapper;

import com.kedong.ieduflswebcommon.entity.FaultVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaultDao {

    List<FaultVo> getSgFaultList();

}
