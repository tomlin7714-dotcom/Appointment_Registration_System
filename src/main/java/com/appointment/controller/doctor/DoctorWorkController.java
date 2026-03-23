package com.appointment.controller.doctor;

import com.appointment.entity.*;
import com.appointment.mapper.*;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor")
public class DoctorWorkController {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private NumberSourceMapper numberSourceMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/schedule/save")
    public ResponseVo saveSchedule(@RequestBody Map<String, Object> params) {
        Integer doctorId = (Integer) params.get("doctorId");
        String date = (String) params.get("date");
        String period = (String) params.get("period");

        if (doctorId == null || date == null || period == null) {
            return ResponseVo.error(400, "参数不完整");
        }

        Schedule schedule = new Schedule();
        schedule.setDoctorId(doctorId);
        schedule.setVisitDate(date);
        schedule.setVisitTime(period);
        schedule.setStatus(0);

        scheduleMapper.insert(schedule);

        Map<String, Object> data = new HashMap<>();
        data.put("id", schedule.getId());

        return ResponseVo.success(data);
    }

    @GetMapping("/schedule/list")
    public ResponseVo getScheduleList(@RequestParam Integer doctorId) {
        List<Schedule> schedules = scheduleMapper.selectByDoctorId(doctorId);
        return ResponseVo.success(schedules);
    }

    @PostMapping("/numbersource/save")
    public ResponseVo saveNumberSource(@RequestBody Map<String, Object> params) {
        Integer scheduleId = (Integer) params.get("scheduleId");
        Integer totalNum = (Integer) params.get("totalNum");
        Integer fee = (Integer) params.get("fee");

        if (scheduleId == null || totalNum == null) {
            return ResponseVo.error(400, "参数不完整");
        }

        NumberSource numberSource = new NumberSource();
        numberSource.setScheduleId(scheduleId);
        numberSource.setTotalNum(totalNum);
        numberSource.setRemainNum(totalNum);
        numberSource.setFee(fee != null ? fee : 0);
        numberSource.setStatus(0);

        numberSourceMapper.insert(numberSource);

        Map<String, Object> data = new HashMap<>();
        data.put("id", numberSource.getId());

        return ResponseVo.success(data);
    }

    @GetMapping("/numbersource/list")
    public ResponseVo getNumberSourceList(@RequestParam Integer doctorId) {
        List<NumberSource> numberSources = numberSourceMapper.selectByDoctorId(doctorId);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (NumberSource source : numberSources) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", source.getId());
            item.put("scheduleId", source.getScheduleId());
            item.put("totalNum", source.getTotalNum());
            item.put("remainNum", source.getRemainNum());
            item.put("fee", source.getFee());
            item.put("status", source.getStatus());
            item.put("createTime", source.getCreateTime());

            Schedule schedule = scheduleMapper.selectById(source.getScheduleId());
            if (schedule != null) {
                item.put("visitDate", schedule.getVisitDate());
                item.put("visitTime", schedule.getVisitTime());
            }

            result.add(item);
        }

        return ResponseVo.success(result);
    }

    @GetMapping("/numbersource/close")
    public ResponseVo closeNumberSource(@RequestParam Integer id) {
        if (id == null) {
            return ResponseVo.error(400, "号源ID不能为空");
        }

        NumberSource numberSource = numberSourceMapper.selectById(id);
        if (numberSource == null) {
            return ResponseVo.error(404, "号源不存在");
        }

        numberSource.setStatus(3);
        numberSourceMapper.update(numberSource);

        return ResponseVo.success();
    }

    @GetMapping("/numbersource/delete")
    public ResponseVo deleteNumberSource(@RequestParam Integer id) {
        if (id == null) {
            return ResponseVo.error(400, "号源ID不能为空");
        }

        NumberSource numberSource = numberSourceMapper.selectById(id);
        if (numberSource == null) {
            return ResponseVo.error(404, "号源不存在");
        }

        Schedule schedule = scheduleMapper.selectById(numberSource.getScheduleId());
        if (schedule == null) {
            return ResponseVo.error(404, "排班信息不存在");
        }

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        boolean isExpired = schedule.getVisitDate().compareTo(today) < 0;

        List<Appointment> appointments = appointmentMapper.selectByNumberSourceId(id);
        
        if (!isExpired && appointments != null && !appointments.isEmpty()) {
            return ResponseVo.error(400, "该号源未过期且有预约记录，无法删除");
        }

        try {
            if (appointments != null && !appointments.isEmpty()) {
                for (Appointment appointment : appointments) {
                    appointmentMapper.delete(appointment.getId());
                }
            }
            
            numberSourceMapper.delete(id);
            return ResponseVo.success();
        } catch (Exception e) {
            return ResponseVo.error(500, "删除失败：" + e.getMessage());
        }
    }

    @GetMapping("/appointment/list")
    public ResponseVo getAppointmentList(@RequestParam Integer doctorId,
                                         @RequestParam(required = false) String date,
                                         @RequestParam(required = false) Integer status) {
        List<Appointment> appointments = appointmentMapper.selectByDoctorId(doctorId);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            NumberSource source = numberSourceMapper.selectById(appointment.getNumberSourceId());
            if (source == null) continue;

            Schedule schedule = scheduleMapper.selectById(source.getScheduleId());
            if (schedule == null) continue;

            if (date != null && !date.isEmpty() && !schedule.getVisitDate().equals(date)) {
                continue;
            }

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
            item.put("visitDate", schedule.getVisitDate());
            item.put("visitTime", schedule.getVisitTime());

            User user = userMapper.selectById(appointment.getUserId());
            if (user != null) {
                item.put("phone", user.getPhone());
            }

            result.add(item);
        }

        return ResponseVo.success(result);
    }

    @GetMapping("/appointment/today")
    public ResponseVo getTodayAppointments(@RequestParam Integer doctorId) {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        List<Appointment> appointments = appointmentMapper.selectByDoctorId(doctorId);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            NumberSource source = numberSourceMapper.selectById(appointment.getNumberSourceId());
            if (source == null) continue;

            Schedule schedule = scheduleMapper.selectById(source.getScheduleId());
            if (schedule == null) continue;

            if (!schedule.getVisitDate().equals(today)) {
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
            item.put("visitDate", schedule.getVisitDate());
            item.put("visitTime", schedule.getVisitTime());
            item.put("numberSourceId", source.getId());

            User user = userMapper.selectById(appointment.getUserId());
            if (user != null) {
                item.put("phone", user.getPhone());
            }

            result.add(item);
        }

        result.sort((a, b) -> {
            Integer statusA = (Integer) a.get("status");
            Integer statusB = (Integer) b.get("status");
            
            if (statusA == 1 && statusB != 1) return -1;
            if (statusA != 1 && statusB == 1) return 1;
            
            if (statusA.equals(statusB)) {
                java.util.Date timeA = (java.util.Date) a.get("appointmentTime");
                java.util.Date timeB = (java.util.Date) b.get("appointmentTime");
                if (timeA != null && timeB != null) {
                    return timeA.compareTo(timeB);
                }
            }
            
            return 0;
        });

        return ResponseVo.success(result);
    }

    @GetMapping("/call/next")
    public ResponseVo callNext(@RequestParam Integer doctorId) {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        List<Appointment> appointments = appointmentMapper.selectByDoctorId(doctorId);
        
        Appointment nextAppointment = null;
        for (Appointment appointment : appointments) {
            if (appointment.getStatus() != 1) continue;

            NumberSource source = numberSourceMapper.selectById(appointment.getNumberSourceId());
            if (source == null) continue;

            Schedule schedule = scheduleMapper.selectById(source.getScheduleId());
            if (schedule == null) continue;

            if (schedule.getVisitDate().equals(today)) {
                if (nextAppointment == null || 
                    appointment.getAppointmentTime().before(nextAppointment.getAppointmentTime())) {
                    nextAppointment = appointment;
                }
            }
        }

        if (nextAppointment == null) {
            return ResponseVo.error(404, "没有待叫号的患者");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("appointmentId", nextAppointment.getId());
        data.put("patientName", nextAppointment.getPatientName());
        data.put("patientIdCard", nextAppointment.getPatientIdCard());
        data.put("fee", nextAppointment.getFee());
        data.put("status", nextAppointment.getStatus());

        User user = userMapper.selectById(nextAppointment.getUserId());
        if (user != null) {
            data.put("phone", user.getPhone());
        }

        NumberSource source = numberSourceMapper.selectById(nextAppointment.getNumberSourceId());
        if (source != null) {
            Schedule schedule = scheduleMapper.selectById(source.getScheduleId());
            if (schedule != null) {
                data.put("visitDate", schedule.getVisitDate());
                data.put("visitTime", schedule.getVisitTime());
            }
        }

        return ResponseVo.success(data);
    }

    @PostMapping("/call/start")
    public ResponseVo startCall(@RequestBody Map<String, Object> params) {
        Integer appointmentId = (Integer) params.get("appointmentId");

        if (appointmentId == null) {
            return ResponseVo.error(400, "预约ID不能为空");
        }

        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            return ResponseVo.error(404, "预约不存在");
        }

        if (appointment.getStatus() != 1) {
            return ResponseVo.error(400, "该患者未支付或已就诊");
        }

        appointment.setStatus(2);
        appointmentMapper.update(appointment);

        return ResponseVo.success();
    }

    @PostMapping("/appointment/complete")
    public ResponseVo completeAppointment(@RequestBody Map<String, Object> params) {
        Integer appointmentId = (Integer) params.get("appointmentId");

        if (appointmentId == null) {
            return ResponseVo.error(400, "预约ID不能为空");
        }

        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            return ResponseVo.error(404, "预约不存在");
        }

        appointment.setStatus(3);
        appointmentMapper.update(appointment);

        return ResponseVo.success();
    }

    @GetMapping("/stats")
    public ResponseVo getStats(@RequestParam Integer doctorId) {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        List<Appointment> appointments = appointmentMapper.selectByDoctorId(doctorId);
        
        int todayTotal = 0;
        int todayCompleted = 0;
        int todayPending = 0;

        for (Appointment appointment : appointments) {
            NumberSource source = numberSourceMapper.selectById(appointment.getNumberSourceId());
            if (source == null) continue;

            Schedule schedule = scheduleMapper.selectById(source.getScheduleId());
            if (schedule == null) continue;

            if (schedule.getVisitDate().equals(today)) {
                todayTotal++;
                if (appointment.getStatus() == 3) {
                    todayCompleted++;
                } else if (appointment.getStatus() == 1 || appointment.getStatus() == 2) {
                    todayPending++;
                }
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("todayTotal", todayTotal);
        stats.put("todayCompleted", todayCompleted);
        stats.put("todayPending", todayPending);

        return ResponseVo.success(stats);
    }

    @GetMapping("/info")
    public ResponseVo getDoctorInfo(@RequestParam Integer doctorId) {
        if (doctorId == null) {
            return ResponseVo.error(400, "医生ID不能为空");
        }

        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            return ResponseVo.error(404, "医生不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", doctor.getId());
        data.put("workNo", doctor.getWorkNo());
        data.put("name", doctor.getName());
        data.put("title", doctor.getTitle());
        data.put("phone", doctor.getPhone());
        data.put("intro", doctor.getIntro());
        data.put("specialty", doctor.getSpecialty());

        Dept dept = deptMapper.selectById(doctor.getDeptId());
        data.put("deptName", dept != null ? dept.getDeptName() : "");

        return ResponseVo.success(data);
    }

    @GetMapping("/schedule/cleanExpired")
    public ResponseVo cleanExpiredSchedules(@RequestParam Integer doctorId) {
        if (doctorId == null) {
            return ResponseVo.error(400, "医生ID不能为空");
        }

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        
        List<Schedule> allSchedules = scheduleMapper.selectByDoctorId(doctorId);
        int deletedCount = 0;
        
        for (Schedule schedule : allSchedules) {
            if (schedule.getVisitDate().compareTo(today) < 0) {
                List<NumberSource> numberSources = numberSourceMapper.selectByScheduleId(schedule.getId());
                
                for (NumberSource ns : numberSources) {
                    List<Appointment> appointments = appointmentMapper.selectByNumberSourceId(ns.getId());
                    if (appointments != null && !appointments.isEmpty()) {
                        for (Appointment appointment : appointments) {
                            appointmentMapper.delete(appointment.getId());
                        }
                    }
                    numberSourceMapper.delete(ns.getId());
                }
                
                scheduleMapper.delete(schedule.getId());
                deletedCount++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", deletedCount);
        return ResponseVo.success(result);
    }
}
