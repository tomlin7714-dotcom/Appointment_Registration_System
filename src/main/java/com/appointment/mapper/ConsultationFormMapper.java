package com.appointment.mapper;

import com.appointment.entity.ConsultationForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ConsultationFormMapper {
    ConsultationForm selectById(@Param("id") Integer id);
    ConsultationForm selectByAppointmentId(@Param("appointmentId") Integer appointmentId);
    List<ConsultationForm> selectByUserId(@Param("userId") Integer userId);
    List<ConsultationForm> searchByUserId(@Param("userId") Integer userId,
                                          @Param("keyword") String keyword);
    int insert(ConsultationForm consultationForm);
    int update(ConsultationForm consultationForm);
}
