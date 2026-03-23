package com.appointment.mapper;

import com.appointment.entity.NumberSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NumberSourceMapper {
    NumberSource selectById(@Param("id") Integer id);
    List<NumberSource> selectByDoctorId(@Param("doctorId") Integer doctorId);
    List<NumberSource> selectByScheduleId(@Param("scheduleId") Integer scheduleId);
    List<NumberSource> selectAll();
    int insert(NumberSource numberSource);
    int update(NumberSource numberSource);
    int delete(@Param("id") Integer id);
}
