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
        Integer doctorId = parseInteger(params.get("doctorId"));
        String date = (String) params.get("date");
        String period = (String) params.get("period");

        if (doctorId == null || date == null || period == null) {
            return ResponseVo.error(400, "参数不完整");
        }

        // 将简写时段转换为完整格式
        String fullPeriod;
        switch (period) {
            case "morning":
                fullPeriod = "上午 08:00-12:00";
                break;
            case "afternoon":
                fullPeriod = "下午 14:00-17:00";
                break;
            case "evening":
                fullPeriod = "晚上 18:00-21:00";
                break;
            default:
                fullPeriod = period;
        }

        // 检查是否已存在
        List<Schedule> existing = scheduleMapper.selectByDoctorIdAndDate(doctorId, date);
        for (Schedule s : existing) {
            if (fullPeriod.equals(s.getVisitTime())) {
                return ResponseVo.error(400, "该日期时段已存在排班");
            }
        }

        Schedule schedule = new Schedule();
        schedule.setDoctorId(doctorId);
        schedule.setVisitDate(date);
        schedule.setVisitTime(fullPeriod);
        schedule.setStatus(0);

        scheduleMapper.insert(schedule);

        // 自动创建号源
        NumberSource numberSource = new NumberSource();
        numberSource.setScheduleId(schedule.getId());
        numberSource.setTotalNum(20);
        numberSource.setRemainNum(20);
        numberSource.setFee(0.0);
        numberSource.setStatus(0);
        numberSourceMapper.insert(numberSource);

        Map<String, Object> data = new HashMap<>();
        data.put("id", schedule.getId());
        data.put("numberSourceId", numberSource.getId());

        return ResponseVo.success(data);
    }

    @GetMapping("/schedule/list")
    public ResponseVo getScheduleList(@RequestParam Integer doctorId) {
        List<Schedule> schedules = scheduleMapper.selectByDoctorId(doctorId);
        return ResponseVo.success(schedules);
    }

    @PostMapping("/numbersource/save")
    public ResponseVo saveNumberSource(@RequestBody Map<String, Object> params) {
        Integer scheduleId = parseInteger(params.get("scheduleId"));
        Integer totalNum = parseInteger(params.get("totalNum"));
        Double fee = parseDouble(params.get("fee"));

        if (scheduleId == null || totalNum == null) {
            return ResponseVo.error(400, "参数不完整");
        }

        NumberSource numberSource = new NumberSource();
        numberSource.setScheduleId(scheduleId);
        numberSource.setTotalNum(totalNum);
        numberSource.setRemainNum(totalNum);
        numberSource.setFee(fee != null ? fee : 0.0);
        numberSource.setStatus(0);

        numberSourceMapper.insert(numberSource);

        Map<String, Object> data = new HashMap<>();
        data.put("id", numberSource.getId());

        return ResponseVo.success(data);
    }

    @GetMapping("/numbersource/list")
    public ResponseVo getNumberSourceList(@RequestParam Integer doctorId,
                                         @RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                         @RequestParam(required = false) String date) {
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
                
                if (date != null && !date.isEmpty() && !schedule.getVisitDate().equals(date)) {
                    continue;
                }
            }

            result.add(item);
        }

        // 按日期升序排列
        result.sort((a, b) -> {
            String dateA = (String) a.get("visitDate");
            String dateB = (String) b.get("visitDate");
            if (dateA != null && dateB != null) {
                return dateA.compareTo(dateB);
            }
            return 0;
        });

        // 分页处理
        int total = result.size();
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<Map<String, Object>> paginatedResult = start < total ? result.subList(start, end) : new ArrayList<>();
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("list", paginatedResult);
        responseData.put("total", total);
        responseData.put("page", page);
        responseData.put("pageSize", pageSize);

        return ResponseVo.success(responseData);
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
                                         @RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                         @RequestParam(required = false) String date,
                                         @RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) String keyword) {
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

            // 模糊查询
            if (keyword != null && !keyword.isEmpty()) {
                boolean nameMatch = appointment.getPatientName() != null && appointment.getPatientName().contains(keyword);
                User user = userMapper.selectById(appointment.getUserId());
                boolean phoneMatch = user != null && user.getPhone() != null && user.getPhone().contains(keyword);
                if (!nameMatch && !phoneMatch) {
                    continue;
                }
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

            Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
            if (doctor != null) {
                Dept dept = deptMapper.selectById(doctor.getDeptId());
                if (dept != null) {
                    item.put("deptName", dept.getDeptName());
                    item.put("area", dept.getArea());
                    item.put("roomNumber", dept.getRoomNumber());
                }
            }

            result.add(item);
        }

        // 按日期降序排列
        result.sort((a, b) -> {
            String dateA = (String) a.get("visitDate");
            String dateB = (String) b.get("visitDate");
            if (dateA != null && dateB != null) {
                return dateB.compareTo(dateA);
            }
            return 0;
        });

        // 分页处理
        int total = result.size();
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<Map<String, Object>> paginatedResult = start < total ? result.subList(start, end) : new ArrayList<>();
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("list", paginatedResult);
        responseData.put("total", total);
        responseData.put("page", page);
        responseData.put("pageSize", pageSize);

        return ResponseVo.success(responseData);
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

            // 添加科室和诊室信息
            Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
            if (doctor != null) {
                Dept dept = deptMapper.selectById(doctor.getDeptId());
                if (dept != null) {
                    item.put("deptName", dept.getDeptName());
                    item.put("area", dept.getArea());
                    item.put("roomNumber", dept.getRoomNumber());
                }
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

                // 添加科室和诊室信息
                Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
                if (doctor != null) {
                    Dept dept = deptMapper.selectById(doctor.getDeptId());
                    if (dept != null) {
                        data.put("deptName", dept.getDeptName());
                        data.put("area", dept.getArea());
                        data.put("roomNumber", dept.getRoomNumber());
                    }
                }
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

    @PostMapping("/schedule/auto")
    public ResponseVo autoSchedule(@RequestBody Map<String, Object> params) {
        Integer doctorId = parseInteger(params.get("doctorId"));

        if (doctorId == null) {
            return ResponseVo.error(400, "医生ID不能为空");
        }

        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            return ResponseVo.error(404, "医生不存在");
        }

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar cal = java.util.Calendar.getInstance();

        String[] periods = {
            "上午 08:00-10:00",
            "上午 10:00-12:00",
            "下午 14:00-16:00",
            "下午 16:00-18:00"
        };

        int createdCount = 0;

        for (int i = 1; i <= 14; i++) {
            cal.setTime(new Date());
            cal.add(java.util.Calendar.DAY_OF_MONTH, i);
            String visitDate = sdf.format(cal.getTime());

            int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
            if (dayOfWeek == java.util.Calendar.SUNDAY) {
                continue;
            }

            for (String period : periods) {
                List<Schedule> existing = scheduleMapper.selectByDoctorIdAndDate(doctorId, visitDate);
                boolean hasPeriod = false;
                for (Schedule s : existing) {
                    if (period.equals(s.getVisitTime())) {
                        hasPeriod = true;
                        break;
                    }
                }

                if (!hasPeriod) {
                    Schedule schedule = new Schedule();
                    schedule.setDoctorId(doctorId);
                    schedule.setVisitDate(visitDate);
                    schedule.setVisitTime(period);
                    schedule.setStatus(0);
                    scheduleMapper.insert(schedule);

                    NumberSource numberSource = new NumberSource();
                    numberSource.setScheduleId(schedule.getId());
                    numberSource.setTotalNum(20);
                    numberSource.setRemainNum(20);
                    numberSource.setFee(0.0);
                    numberSource.setStatus(0);
                    numberSourceMapper.insert(numberSource);

                    createdCount++;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("createdCount", createdCount);

        return ResponseVo.success(result);
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

    @GetMapping("/work/dashboard")
    public ResponseVo getDashboard(@RequestParam Integer doctorId) {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        Map<String, Object> data = new HashMap<>();
        
        // 获取统计数据
        List<Appointment> allAppointments = appointmentMapper.selectByDoctorId(doctorId);
        int todayTotal = 0;
        int todayCompleted = 0;
        int todayPending = 0;
        
        for (Appointment appointment : allAppointments) {
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
        stats.put("todayAppoint", todayTotal);
        stats.put("waitingCount", todayPending);
        stats.put("completedCount", todayCompleted);
        
        // 获取本周排班数
        List<Schedule> schedules = scheduleMapper.selectByDoctorId(doctorId);
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(new Date());
        int weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
        
        int weekScheduleCount = 0;
        for (Schedule schedule : schedules) {
            Calendar sCal = Calendar.getInstance();
            sCal.setFirstDayOfWeek(Calendar.MONDAY);
            sCal.setTime(java.sql.Date.valueOf(schedule.getVisitDate()));
            if (sCal.get(Calendar.WEEK_OF_YEAR) == weekOfYear) {
                weekScheduleCount++;
            }
        }
        stats.put("totalSchedule", weekScheduleCount);
        
        data.put("stats", stats);
        
        // 获取今日预约列表
        List<Map<String, Object>> todayList = new ArrayList<>();
        for (Appointment appointment : allAppointments) {
            NumberSource source = numberSourceMapper.selectById(appointment.getNumberSourceId());
            if (source == null) continue;
            
            Schedule schedule = scheduleMapper.selectById(source.getScheduleId());
            if (schedule == null) continue;
            
            if (!schedule.getVisitDate().equals(today)) continue;
            
            Map<String, Object> item = new HashMap<>();
            item.put("id", appointment.getId());
            item.put("appointmentNo", appointment.getId());
            item.put("patientName", appointment.getPatientName());
            item.put("appointmentTime", appointment.getAppointmentTime());
            item.put("visitDate", schedule.getVisitDate());
            item.put("visitTime", schedule.getVisitTime());
            item.put("status", appointment.getStatus());
            
            todayList.add(item);
        }
        
        // 按预约时间排序
        todayList.sort((a, b) -> {
            java.util.Date timeA = (java.util.Date) a.get("appointmentTime");
            java.util.Date timeB = (java.util.Date) b.get("appointmentTime");
            if (timeA != null && timeB != null) {
                return timeA.compareTo(timeB);
            }
            return 0;
        });
        
        data.put("todayList", todayList);
        
        return ResponseVo.success(data);
    }

    @GetMapping("/profile/get")
    public ResponseVo getProfile(@RequestParam Integer doctorId) {
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
        data.put("remark", doctor.getIntro());
        data.put("specialty", doctor.getSpecialty());

        Dept dept = deptMapper.selectById(doctor.getDeptId());
        data.put("deptName", dept != null ? dept.getDeptName() : "");

        return ResponseVo.success(data);
    }

    @PostMapping("/profile/update")
    public ResponseVo updateProfile(@RequestBody Map<String, Object> params) {
        Integer doctorId = (Integer) params.get("id");
        String name = (String) params.get("name");
        String phone = (String) params.get("phone");
        String title = (String) params.get("title");
        String remark = (String) params.get("remark");
        String specialty = (String) params.get("specialty");

        if (doctorId == null) {
            return ResponseVo.error(400, "医生ID不能为空");
        }

        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            return ResponseVo.error(404, "医生不存在");
        }

        if (name != null) {
            doctor.setName(name);
        }
        if (phone != null) {
            doctor.setPhone(phone);
        }
        if (title != null) {
            doctor.setTitle(title);
        }
        if (remark != null) {
            doctor.setIntro(remark);
        }
        if (specialty != null) {
            doctor.setSpecialty(specialty);
        }

        doctorMapper.update(doctor);

        return ResponseVo.success();
    }

    @PostMapping("/profile/changePwd")
    public ResponseVo changePassword(@RequestBody Map<String, String> params) {
        String oldPwd = params.get("oldPwd");
        String newPwd = params.get("newPwd");
        Integer doctorId = Integer.parseInt(params.get("doctorId"));

        if (oldPwd == null || newPwd == null || doctorId == null) {
            return ResponseVo.error(400, "参数不完整");
        }

        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            return ResponseVo.error(404, "医生不存在");
        }

        if (!doctor.getPwd().equals(oldPwd)) {
            return ResponseVo.error(400, "旧密码错误");
        }

        doctor.setPwd(newPwd);
        doctorMapper.update(doctor);

        return ResponseVo.success();
    }

    private Double parseDouble(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Double) return (Double) obj;
        if (obj instanceof Integer) return ((Integer) obj).doubleValue();
        if (obj instanceof String) {
            try {
                return Double.parseDouble((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        return null;
    }
}
