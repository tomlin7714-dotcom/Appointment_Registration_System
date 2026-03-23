package com.appointment.mapper;

import com.appointment.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {
    Admin selectByAccount(@Param("adminAccount") String adminAccount);
    Admin selectById(@Param("id") Integer id);
    int insert(Admin admin);
    int update(Admin admin);
    int updateLastLoginTime(@Param("id") Integer id);
}
