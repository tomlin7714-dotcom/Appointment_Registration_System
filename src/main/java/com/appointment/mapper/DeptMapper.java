package com.appointment.mapper;

import com.appointment.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DeptMapper {
    Dept selectById(@Param("id") Integer id);
    List<Dept> selectAll();
    int insert(Dept dept);
    int update(Dept dept);
    int delete(@Param("id") Integer id);
}
