package com.appointment.mapper;

import com.appointment.entity.OperateLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OperateLogMapper {
    int insert(OperateLog log);
    List<OperateLog> selectAll();
    List<OperateLog> selectByOperatorId(@Param("operatorId") Integer operatorId);
}
