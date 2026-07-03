package com.appointment.controller.user;

import com.appointment.entity.Appointment;
import com.appointment.entity.ConsultationForm;
import com.appointment.entity.Doctor;
import com.appointment.entity.Schedule;
import com.appointment.entity.NumberSource;
import com.appointment.mapper.*;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/user/consultation")
public class ConsultationFormController {

    @Autowired
    private ConsultationFormMapper consultationFormMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    /**
     * 保存问诊信息（预约后补充填写）
     */
    @PostMapping("/save")
    public ResponseVo save(@RequestBody ConsultationForm form) {
        if (form.getAppointmentId() == null) {
            return ResponseVo.error(400, "预约ID不能为空");
        }

        try {
            // 检查是否已存在
            ConsultationForm existing = consultationFormMapper.selectByAppointmentId(form.getAppointmentId());
            if (existing != null) {
                existing.setFormType(form.getFormType());
                existing.setChiefComplaint(form.getChiefComplaint());
                existing.setPresentIllness(form.getPresentIllness());
                existing.setPastHistory(form.getPastHistory());
                consultationFormMapper.update(existing);
                return ResponseVo.success(existing);
            }

            // 新创建，补充信息
            Appointment appointment = appointmentMapper.selectById(form.getAppointmentId());
            if (appointment == null) {
                return ResponseVo.error(404, "预约不存在");
            }

            // 自动生成问诊编号
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            form.setConsultationNo("CF" + sdf.format(new Date()));

            // 设置user_id和doctor_id
            form.setUserId(appointment.getUserId());

            NumberSource ns = null;
            if (appointment.getNumberSourceId() != null) {
                ns = numberSourceMapper.selectById(appointment.getNumberSourceId());
                if (ns != null) {
                    Schedule schedule = scheduleMapper.selectById(ns.getScheduleId());
                    if (schedule != null) {
                        form.setDoctorId(schedule.getDoctorId());
                    }
                }
            }

            consultationFormMapper.insert(form);
            return ResponseVo.success(form);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVo.error(500, "保存问诊单失败: " + e.getMessage());
        }
    }

    @Autowired
    private NumberSourceMapper numberSourceMapper;
    @Autowired
    private ScheduleMapper scheduleMapper;

    /**
     * 获取问诊详情
     */
    @GetMapping("/detail")
    public ResponseVo getDetail(@RequestParam Integer appointmentId) {
        if (appointmentId == null) {
            return ResponseVo.error(400, "预约ID不能为空");
        }

        try {
            ConsultationForm form = consultationFormMapper.selectByAppointmentId(appointmentId);
            if (form == null) {
                return ResponseVo.error(404, "问诊单不存在");
            }

            // 补充患者和医生信息
            Appointment appointment = appointmentMapper.selectById(appointmentId);
            if (appointment != null) {
                form.setPatientName(appointment.getPatientName());
                form.setPatientIdCard(appointment.getPatientIdCard());
                form.setAppointmentStatus(appointment.getStatus());
            }

            if (form.getDoctorId() != null) {
                Doctor doctor = doctorMapper.selectById(form.getDoctorId());
                if (doctor != null) {
                    form.setDoctorName(doctor.getName());
                    form.setDoctorTitle(doctor.getTitle());
                }
            }

            return ResponseVo.success(form);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVo.error(500, "查询问诊单失败: " + e.getMessage());
        }
    }

    /**
     * 获取问诊历史列表（含搜索）
     */
    @GetMapping("/list")
    public ResponseVo list(@RequestParam Integer userId,
                           @RequestParam(required = false) String keyword) {
        if (userId == null) {
            return ResponseVo.error(400, "用户ID不能为空");
        }

        try {
            List<ConsultationForm> list;
            if (keyword != null && !keyword.isEmpty()) {
                list = consultationFormMapper.searchByUserId(userId, keyword);
            } else {
                list = consultationFormMapper.selectByUserId(userId);
            }

            return ResponseVo.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVo.error(500, "查询问诊列表失败: " + e.getMessage());
        }
    }
}
