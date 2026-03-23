package com.appointment.mapper;

import com.appointment.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DoctorMapper {
    Doctor selectById(@Param("id") Integer id);
    Doctor selectByWorkNo(@Param("workNo") String workNo);
    List<Doctor> selectAll();
    List<Doctor> selectByDeptId(@Param("deptId") Integer deptId);
    int insert(Doctor doctor);
    int update(Doctor doctor);
    int delete(@Param("id") Integer id);
    int updateLastLoginTime(@Param("id") Integer id);
}
