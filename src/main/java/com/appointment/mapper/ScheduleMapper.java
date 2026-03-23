package com.appointment.mapper;

import com.appointment.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ScheduleMapper {
    Schedule selectById(@Param("id") Integer id);
    List<Schedule> selectByDoctorId(@Param("doctorId") Integer doctorId);
    List<Schedule> selectByDoctorIdAndDate(@Param("doctorId") Integer doctorId, @Param("visitDate") String visitDate);
    List<Schedule> selectAll();
    int insert(Schedule schedule);
    int update(Schedule schedule);
    int delete(@Param("id") Integer id);
}
