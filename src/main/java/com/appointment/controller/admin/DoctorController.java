package com.appointment.controller.admin;

import com.appointment.entity.Dept;
import com.appointment.entity.Doctor;
import com.appointment.entity.Schedule;
import com.appointment.entity.NumberSource;
import com.appointment.entity.OperateLog;
import com.appointment.mapper.DeptMapper;
import com.appointment.mapper.DoctorMapper;
import com.appointment.mapper.ScheduleMapper;
import com.appointment.mapper.NumberSourceMapper;
import com.appointment.mapper.OperateLogMapper;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/doctor")
public class DoctorController {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private NumberSourceMapper numberSourceMapper;

    @Autowired
    private OperateLogMapper operateLogMapper;

    @GetMapping("/list")
    public ResponseVo list(@RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Integer deptId,
                          @RequestParam(required = false) Integer status) {
        List<Doctor> doctors = doctorMapper.selectAll();
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Doctor doctor : doctors) {
            boolean match = true;
            
            if (keyword != null && !keyword.isEmpty()) {
                if (!doctor.getName().contains(keyword) && !doctor.getWorkNo().contains(keyword)) {
                    match = false;
                }
            }
            if (deptId != null && !doctor.getDeptId().equals(deptId)) {
                match = false;
            }
            if (status != null && !doctor.getStatus().equals(status)) {
                match = false;
            }
            
            if (match) {
                Map<String, Object> doctorMap = new HashMap<>();
                doctorMap.put("id", doctor.getId());
                doctorMap.put("name", doctor.getName());
                doctorMap.put("doctorAccount", doctor.getWorkNo());
                doctorMap.put("deptId", doctor.getDeptId());
                doctorMap.put("title", doctor.getTitle());
                doctorMap.put("phone", doctor.getPhone());
                doctorMap.put("status", doctor.getStatus());
                
                Dept dept = deptMapper.selectById(doctor.getDeptId());
                doctorMap.put("deptName", dept != null ? dept.getDeptName() : "-");
                
                result.add(doctorMap);
            }
        }

        return ResponseVo.success(result);
    }

    @PostMapping("/save")
    public ResponseVo save(@RequestBody Map<String, Object> params) {
        String name = (String) params.get("name");
        String doctorAccount = (String) params.get("doctorAccount");
        String pwd = (String) params.get("pwd");
        Integer deptId = parseInteger(params.get("deptId"));
        String title = (String) params.get("title");
        String phone = (String) params.get("phone");
        String remark = (String) params.get("remark");

        if (doctorAccount == null || doctorAccount.isEmpty()) {
            return ResponseVo.error(400, "工号不能为空");
        }
        if (name == null || name.isEmpty()) {
            return ResponseVo.error(400, "姓名不能为空");
        }
        if (deptId == null) {
            return ResponseVo.error(400, "所属科室不能为空");
        }

        Doctor existDoctor = doctorMapper.selectByWorkNo(doctorAccount);
        if (existDoctor != null) {
            return ResponseVo.error(400, "工号已存在");
        }

        Doctor doctor = new Doctor();
        doctor.setWorkNo(doctorAccount);
        doctor.setName(name);
        doctor.setPwd(pwd != null && !pwd.isEmpty() ? pwd : "123456");
        doctor.setDeptId(deptId);
        doctor.setTitle(title != null ? title : "");
        doctor.setPhone(phone != null ? phone : "");
        doctor.setIntro(remark != null ? remark : "");
        doctor.setSpecialty("");
        doctor.setGender("男");
        doctor.setStatus(0);

        doctorMapper.insert(doctor);

        Map<String, Object> data = new HashMap<>();
        data.put("id", doctor.getId());

        OperateLog log = new OperateLog();
        log.setOperatorId(1);
        log.setOperatorRole(2);
        log.setContent("新增医生：" + name + "(" + doctorAccount + ")");
        log.setOperateIp("127.0.0.1");
        operateLogMapper.insert(log);

        return ResponseVo.success(data);
    }

    private Integer parseInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        if (obj instanceof Number) return ((Number) obj).intValue();
        return null;
    }

    @PostMapping("/modify")
    public ResponseVo modify(@RequestBody Map<String, Object> params) {
        Integer id = parseInteger(params.get("id"));
        String name = (String) params.get("name");
        String pwd = (String) params.get("pwd");
        Integer deptId = parseInteger(params.get("deptId"));
        String title = (String) params.get("title");
        String phone = (String) params.get("phone");
        String remark = (String) params.get("remark");
        Integer status = parseInteger(params.get("status"));

        if (id == null) {
            return ResponseVo.error(400, "医生ID不能为空");
        }

        Doctor doctor = doctorMapper.selectById(id);
        if (doctor == null) {
            return ResponseVo.error(404, "医生不存在");
        }

        if (name != null && !name.isEmpty()) {
            doctor.setName(name);
        }
        if (pwd != null && !pwd.isEmpty()) {
            doctor.setPwd(pwd);
        }
        if (deptId != null) {
            doctor.setDeptId(deptId);
        }
        if (title != null) {
            doctor.setTitle(title);
        }
        if (phone != null) {
            doctor.setPhone(phone);
        }
        if (remark != null) {
            doctor.setIntro(remark);
        }
        if (status != null) {
            doctor.setStatus(status);
        }

        doctorMapper.update(doctor);

        OperateLog log = new OperateLog();
        log.setOperatorId(1);
        log.setOperatorRole(2);
        log.setContent("修改医生信息：" + doctor.getName());
        log.setOperateIp("127.0.0.1");
        operateLogMapper.insert(log);

        return ResponseVo.success();
    }

    @GetMapping("/del")
    public ResponseVo delete(@RequestParam Integer id) {
        if (id == null) {
            return ResponseVo.error(400, "医生ID不能为空");
        }

        Doctor doctor = doctorMapper.selectById(id);
        if (doctor == null) {
            return ResponseVo.error(404, "医生不存在");
        }

        String doctorName = doctor.getName();
        doctorMapper.delete(id);

        OperateLog log = new OperateLog();
        log.setOperatorId(1);
        log.setOperatorRole(2);
        log.setContent("删除医生：" + doctorName);
        log.setOperateIp("127.0.0.1");
        operateLogMapper.insert(log);

        return ResponseVo.success();
    }

    @PostMapping("/autoSchedule")
    public ResponseVo autoSchedule(@RequestBody Map<String, Object> params) {
        Integer doctorId = (Integer) params.get("doctorId");
        
        if (doctorId == null) {
            return ResponseVo.error(400, "医生ID不能为空");
        }

        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            return ResponseVo.error(404, "医生不存在");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        
        int createdCount = 0;
        
        for (int i = 1; i <= 14; i++) {
            cal.setTime(new java.util.Date());
            cal.add(Calendar.DAY_OF_MONTH, i);
            String visitDate = sdf.format(cal.getTime());
            
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SUNDAY) {
                continue;
            }
            
            String[] periods = {
                "上午 08:00-10:00",
                "上午 10:00-12:00",
                "下午 14:00-16:00",
                "下午 16:00-18:00"
            };
            
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
                    numberSource.setFee(0);
                    numberSource.setStatus(0);
                    numberSourceMapper.insert(numberSource);
                    
                    createdCount++;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("createdCount", createdCount);

        OperateLog log = new OperateLog();
        log.setOperatorId(1);
        log.setOperatorRole(2);
        log.setContent("为医生[" + doctor.getName() + "]自动排班，创建" + createdCount + "个排班");
        log.setOperateIp("127.0.0.1");
        operateLogMapper.insert(log);

        return ResponseVo.success(result);
    }
}
