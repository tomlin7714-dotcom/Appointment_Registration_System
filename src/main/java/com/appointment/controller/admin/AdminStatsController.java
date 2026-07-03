package com.appointment.controller.admin;

import com.appointment.mapper.*;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private NumberSourceMapper numberSourceMapper;

    @GetMapping("/overview")
    public ResponseVo getOverviewStats() {
        Map<String, Object> stats = new HashMap<>();

        // 科室数量
        List<?> depts = deptMapper.selectAll();
        stats.put("deptCount", depts != null ? depts.size() : 0);

        // 医生数量
        List<?> doctors = doctorMapper.selectAll();
        stats.put("doctorCount", doctors != null ? doctors.size() : 0);

        // 用户数量
        List<?> users = userMapper.selectAll();
        stats.put("userCount", users != null ? users.size() : 0);

        // 今日预约数量
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        int todayCount = appointmentMapper.countTodayAppointments(today);
        stats.put("appointmentCount", todayCount);

        // 总预约数量
        int totalAppointments = appointmentMapper.countAll();
        stats.put("totalAppointments", totalAppointments);

        // 总排班数量
        List<?> schedules = scheduleMapper.selectAll();
        stats.put("scheduleCount", schedules != null ? schedules.size() : 0);

        // 总号源数量
        List<?> numberSources = numberSourceMapper.selectAll();
        stats.put("numberSourceCount", numberSources != null ? numberSources.size() : 0);

        return ResponseVo.success(stats);
    }

    @GetMapping("/todayCount")
    public ResponseVo getTodayAppointmentCount() {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        int count = appointmentMapper.countTodayAppointments(today);
        return ResponseVo.success(count);
    }
}
