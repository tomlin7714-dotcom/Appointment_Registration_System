package com.appointment.controller.user;

import com.appointment.entity.*;
import com.appointment.mapper.*;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/user/appointment")
public class AppointmentController {

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private NumberSourceMapper numberSourceMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @GetMapping("/dept/list")
    public ResponseVo getDeptList() {
        List<Dept> deptList = deptMapper.selectAll();
        return ResponseVo.success(deptList);
    }

    @GetMapping("/doctor/list")
    public ResponseVo getDoctorList(@RequestParam(required = false) Integer deptId,
                                    @RequestParam(required = false) String keyword) {
        List<Doctor> doctorList;
        if (deptId != null) {
            doctorList = doctorMapper.selectByDeptId(deptId);
        } else {
            doctorList = doctorMapper.selectAll();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Doctor doctor : doctorList) {
            if (keyword != null && !doctor.getName().contains(keyword)) {
                continue;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("id", doctor.getId());
            item.put("name", doctor.getName());
            item.put("title", doctor.getTitle());
            item.put("deptId", doctor.getDeptId());
            item.put("intro", doctor.getIntro());
            item.put("specialty", doctor.getSpecialty());

            Dept dept = deptMapper.selectById(doctor.getDeptId());
            item.put("deptName", dept != null ? dept.getDeptName() : "");

            result.add(item);
        }
        return ResponseVo.success(result);
    }

    @GetMapping("/schedule/list")
    public ResponseVo getScheduleList(@RequestParam Integer doctorId) {
        if (doctorId == null) {
            return ResponseVo.error(400, "医生ID不能为空");
        }

        List<Schedule> scheduleList = scheduleMapper.selectByDoctorId(doctorId);
        return ResponseVo.success(scheduleList);
    }

    @GetMapping("/numberSource/list")
    public ResponseVo getNumberSourceList(@RequestParam(required = false) Integer doctorId,
                                          @RequestParam(required = false) Integer scheduleId) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (scheduleId != null) {
            List<NumberSource> sourceList = numberSourceMapper.selectByScheduleId(scheduleId);
            for (NumberSource source : sourceList) {
                Map<String, Object> item = convertNumberSource(source);
                result.add(item);
            }
        } else if (doctorId != null) {
            List<NumberSource> sourceList = numberSourceMapper.selectByDoctorId(doctorId);
            for (NumberSource source : sourceList) {
                Map<String, Object> item = convertNumberSource(source);
                result.add(item);
            }
        } else {
            List<NumberSource> sourceList = numberSourceMapper.selectAll();
            for (NumberSource source : sourceList) {
                Map<String, Object> item = convertNumberSource(source);
                result.add(item);
            }
        }

        return ResponseVo.success(result);
    }

    private Map<String, Object> convertNumberSource(NumberSource source) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", source.getId());
        item.put("scheduleId", source.getScheduleId());
        item.put("totalNum", source.getTotalNum());
        item.put("remainNum", source.getRemainNum());
        item.put("fee", source.getFee() != null ? source.getFee() : 0.01);
        item.put("status", source.getStatus());

        Schedule schedule = scheduleMapper.selectById(source.getScheduleId());
        if (schedule != null) {
            item.put("doctorId", schedule.getDoctorId());
            item.put("visitDate", schedule.getVisitDate());
            item.put("visitTime", schedule.getVisitTime());

            Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
            if (doctor != null) {
                item.put("doctorName", doctor.getName());
                item.put("doctorTitle", doctor.getTitle());
                item.put("deptId", doctor.getDeptId());

                Dept dept = deptMapper.selectById(doctor.getDeptId());
                item.put("deptName", dept != null ? dept.getDeptName() : "");
            }
        }

        return item;
    }

    private Integer parseInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof String) return Integer.parseInt((String) obj);
        if (obj instanceof Number) return ((Number) obj).intValue();
        return null;
    }

    @PostMapping("/create")
    public ResponseVo createAppointment(@RequestBody Map<String, Object> params) {
        Integer numberSourceId = parseInteger(params.get("numberSourceId"));
        Integer userId = parseInteger(params.get("userId"));
        String patientName = (String) params.get("patientName");
        String patientIdCard = (String) params.get("patientIdCard");

        if (numberSourceId == null || userId == null || patientName == null || patientIdCard == null) {
            return ResponseVo.error(400, "参数错误：numberSourceId=" + numberSourceId + ", userId=" + userId);
        }

        NumberSource numberSource = numberSourceMapper.selectById(numberSourceId);
        if (numberSource == null) {
            return ResponseVo.error(404, "号源不存在");
        }

        if (numberSource.getRemainNum() <= 0) {
            return ResponseVo.error(400, "号源已约满");
        }

        Appointment appointment = new Appointment();
        appointment.setNumberSourceId(numberSourceId);
        appointment.setUserId(userId);
        appointment.setPatientName(patientName);
        appointment.setPatientIdCard(patientIdCard);
        appointment.setFee(numberSource.getFee() != null ? numberSource.getFee() : 0.01);
        appointment.setStatus(0);
        appointment.setAppointmentTime(new Date());

        appointmentMapper.insert(appointment);

        numberSource.setRemainNum(numberSource.getRemainNum() - 1);
        numberSourceMapper.update(numberSource);

        Map<String, Object> data = new HashMap<>();
        data.put("appointmentId", appointment.getId());
        data.put("fee", 0.01);

        return ResponseVo.success(data);
    }

    @PostMapping("/pay")
    public ResponseVo pay(@RequestBody Map<String, Object> params) {
        Integer appointmentId = parseInteger(params.get("appointmentId"));
        String payType = (String) params.get("payType");

        if (appointmentId == null) {
            return ResponseVo.error(400, "预约ID不能为空");
        }

        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            return ResponseVo.error(404, "预约不存在");
        }

        if (appointment.getStatus() != 0) {
            return ResponseVo.error(400, "该预约状态异常，无法支付");
        }

        appointment.setStatus(1);
        appointment.setPayTime(new Date());
        appointmentMapper.update(appointment);

        Map<String, Object> data = new HashMap<>();
        data.put("appointmentId", appointmentId);
        data.put("status", "success");

        return ResponseVo.success(data);
    }

    @GetMapping("/list")
    public ResponseVo getAppointmentList(@RequestParam Integer userId,
                                         @RequestParam(required = false) Integer status) {
        if (userId == null) {
            return ResponseVo.error(400, "用户ID不能为空");
        }

        List<Appointment> appointmentList = appointmentMapper.selectByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Appointment appointment : appointmentList) {
            if (status != null && !appointment.getStatus().equals(status)) {
                continue;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("id", appointment.getId());
            item.put("patientName", appointment.getPatientName());
            item.put("patientIdCard", appointment.getPatientIdCard());
            item.put("fee", appointment.getFee());
            item.put("status", appointment.getStatus());
            item.put("appointmentTime", appointment.getAppointmentTime());
            item.put("payTime", appointment.getPayTime());

            NumberSource source = numberSourceMapper.selectById(appointment.getNumberSourceId());
            if (source != null) {
                Schedule schedule = scheduleMapper.selectById(source.getScheduleId());
                if (schedule != null) {
                    item.put("visitDate", schedule.getVisitDate());
                    item.put("visitTime", schedule.getVisitTime());

                    Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
                    if (doctor != null) {
                        item.put("doctorName", doctor.getName());
                        item.put("doctorTitle", doctor.getTitle());
                        item.put("doctorId", doctor.getId());

                        Dept dept = deptMapper.selectById(doctor.getDeptId());
                        item.put("deptName", dept != null ? dept.getDeptName() : "");
                        item.put("area", dept != null ? dept.getArea() : "");
                        item.put("roomNumber", dept != null ? dept.getRoomNumber() : "");
                    }
                }
            }

            result.add(item);
        }

        return ResponseVo.success(result);
    }

    @GetMapping("/detail")
    public ResponseVo getAppointmentDetail(@RequestParam Integer appointmentId) {
        if (appointmentId == null) {
            return ResponseVo.error(400, "预约ID不能为空");
        }

        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            return ResponseVo.error(404, "预约不存在");
        }

        Map<String, Object> item = new HashMap<>();
        item.put("id", appointment.getId());
        item.put("patientName", appointment.getPatientName());
        item.put("patientIdCard", appointment.getPatientIdCard());
        item.put("fee", appointment.getFee());
        item.put("status", appointment.getStatus());
        item.put("appointmentTime", appointment.getAppointmentTime());
        item.put("payTime", appointment.getPayTime());

        NumberSource source = numberSourceMapper.selectById(appointment.getNumberSourceId());
        if (source != null) {
            item.put("numberSourceId", source.getId());
            item.put("remainNum", source.getRemainNum());

            Schedule schedule = scheduleMapper.selectById(source.getScheduleId());
            if (schedule != null) {
                item.put("visitDate", schedule.getVisitDate());
                item.put("visitTime", schedule.getVisitTime());

                Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
                if (doctor != null) {
                    item.put("doctorName", doctor.getName());
                    item.put("doctorTitle", doctor.getTitle());
                    item.put("doctorId", doctor.getId());
                    item.put("doctorIntro", doctor.getIntro());

                    Dept dept = deptMapper.selectById(doctor.getDeptId());
                    item.put("deptName", dept != null ? dept.getDeptName() : "");
                    item.put("deptId", doctor.getDeptId());
                    item.put("area", dept != null ? dept.getArea() : "");
                    item.put("roomNumber", dept != null ? dept.getRoomNumber() : "");
                }
            }
        }

        return ResponseVo.success(item);
    }

    @PostMapping("/cancel")
    public ResponseVo cancelAppointment(@RequestBody Map<String, Object> params) {
        Integer appointmentId = (Integer) params.get("appointmentId");

        if (appointmentId == null) {
            return ResponseVo.error(400, "参数错误");
        }

        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            return ResponseVo.error(404, "预约不存在");
        }

        if (appointment.getStatus() == 2 || appointment.getStatus() == 3 || appointment.getStatus() == 4) {
            return ResponseVo.error(400, "该预约已完成或已取消，无法取消");
        }

        appointment.setStatus(4);
        appointmentMapper.update(appointment);

        NumberSource source = numberSourceMapper.selectById(appointment.getNumberSourceId());
        if (source != null) {
            source.setRemainNum(source.getRemainNum() + 1);
            numberSourceMapper.update(source);
        }

        return ResponseVo.success();
    }

    @GetMapping("/stats")
    public ResponseVo getStats(@RequestParam Integer userId) {
        if (userId == null) {
            return ResponseVo.error(400, "用户ID不能为空");
        }

        List<Appointment> appointmentList = appointmentMapper.selectByUserId(userId);

        int pending = 0;
        int completed = 0;
        int total = appointmentList.size();

        for (Appointment appointment : appointmentList) {
            if (appointment.getStatus() == 0 || appointment.getStatus() == 1) {
                pending++;
            } else if (appointment.getStatus() == 2 || appointment.getStatus() == 3) {
                completed++;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", pending);
        stats.put("completed", completed);
        stats.put("total", total);

        return ResponseVo.success(stats);
    }
}
