package com.appointment.mapper;

import com.appointment.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AppointmentMapper {
    Appointment selectById(@Param("id") Integer id);
    List<Appointment> selectByDoctorId(@Param("doctorId") Integer doctorId);
    List<Appointment> selectByUserId(@Param("userId") Integer userId);
    List<Appointment> selectByNumberSourceId(@Param("numberSourceId") Integer numberSourceId);
    List<Appointment> selectValidByNumberSourceId(@Param("numberSourceId") Integer numberSourceId);
    List<Appointment> selectAll();
    int insert(Appointment appointment);
    int update(Appointment appointment);
    int delete(@Param("id") Integer id);
    int countTodayAppointments(@Param("date") String date);
    int countAll();
}
