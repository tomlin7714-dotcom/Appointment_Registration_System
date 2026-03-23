package com.appointment.mapper;

import com.appointment.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    User selectById(@Param("id") Integer id);
    User selectByPhone(@Param("phone") String phone);
    List<User> selectAll();
    int insert(User user);
    int update(User user);
    int delete(@Param("id") Integer id);
    int updateLastLoginTime(@Param("id") Integer id);
}
