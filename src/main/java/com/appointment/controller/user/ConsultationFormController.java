package com.appointment.controller.user;

import com.appointment.entity.Appointment;
import com.appointment.entity.ConsultationForm;
import com.appointment.mapper.AppointmentMapper;
import com.appointment.mapper.ConsultationFormMapper;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/consultation")
public class ConsultationFormController {

    @Autowired
    private ConsultationFormMapper consultationFormMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    /**
     * 保存问诊信息（预约后补充填写）
     */
    @PostMapping("/save")
    public ResponseVo save(@RequestBody ConsultationForm form) {
        if (form.getAppointmentId() == null) {
            return ResponseVo.error(400, "预约ID不能为空");
        }

        // 检查是否已存在
        ConsultationForm existing = consultationFormMapper.selectByAppointmentId(form.getAppointmentId());
        if (existing != null) {
            existing.setConsultationType(form.getConsultationType());
            existing.setChiefComplaint(form.getChiefComplaint());
            existing.setMedicalHistory(form.getMedicalHistory());
            existing.setRecoveryHistory(form.getRecoveryHistory());
            consultationFormMapper.update(existing);
            return ResponseVo.success(existing);
        }

        consultationFormMapper.insert(form);
        return ResponseVo.success(form);
    }

    /**
     * 获取问诊详情
     */
    @GetMapping("/detail")
    public ResponseVo getDetail(@RequestParam Integer appointmentId) {
        if (appointmentId == null) {
            return ResponseVo.error(400, "预约ID不能为空");
        }

        ConsultationForm form = consultationFormMapper.selectByAppointmentId(appointmentId);
        if (form == null) {
            return ResponseVo.error(404, "问诊单不存在");
        }

        // 补充患者信息
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment != null) {
            form.setPatientName(appointment.getPatientName());
            form.setPatientIdCard(appointment.getPatientIdCard());
            form.setStatus(appointment.getStatus());
        }

        return ResponseVo.success(form);
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

        List<ConsultationForm> list;
        if (keyword != null && !keyword.isEmpty()) {
            list = consultationFormMapper.searchByUserId(userId, keyword);
        } else {
            list = consultationFormMapper.selectByUserId(userId);
        }

        return ResponseVo.success(list);
    }
}
