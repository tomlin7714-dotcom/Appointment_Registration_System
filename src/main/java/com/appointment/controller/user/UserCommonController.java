package com.appointment.controller.user;

import com.appointment.entity.*;
import com.appointment.mapper.*;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserCommonController {

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private NumberSourceMapper numberSourceMapper;

    @GetMapping("/dept/list")
    public ResponseVo getDeptList() {
        List<Dept> deptList = deptMapper.selectAll();
        return ResponseVo.success(deptList);
    }

    @GetMapping("/doctor/list")
    public ResponseVo getDoctorList(@RequestParam(required = false) Integer deptId,
                                    @RequestParam(required = false) String date) {
        List<Doctor> doctorList;
        if (deptId != null) {
            doctorList = doctorMapper.selectByDeptId(deptId);
        } else {
            doctorList = doctorMapper.selectAll();
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Doctor doctor : doctorList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", doctor.getId());
            item.put("name", doctor.getName());
            item.put("title", doctor.getTitle());
            item.put("specialty", doctor.getSpecialty());
            item.put("intro", doctor.getIntro());
            
            Dept dept = deptMapper.selectById(doctor.getDeptId());
            item.put("deptId", doctor.getDeptId());
            item.put("deptName", dept != null ? dept.getDeptName() : "");
            
            boolean hasSchedule = false;
            if (date != null && !date.isEmpty()) {
                List<Schedule> schedules = scheduleMapper.selectByDoctorIdAndDate(doctor.getId(), date);
                if (schedules != null && !schedules.isEmpty()) {
                    for (Schedule schedule : schedules) {
                        List<NumberSource> numberSources = numberSourceMapper.selectByScheduleId(schedule.getId());
                        if (numberSources != null) {
                            for (NumberSource ns : numberSources) {
                                if (ns.getRemainNum() > 0) {
                                    hasSchedule = true;
                                    break;
                                }
                            }
                        }
                        if (hasSchedule) break;
                    }
                }
            }
            item.put("hasSchedule", hasSchedule);
            
            result.add(item);
        }
        
        return ResponseVo.success(result);
    }

    @GetMapping("/schedule/list")
    public ResponseVo getScheduleList(@RequestParam Integer doctorId, 
                                       @RequestParam(required = false) String date) {
        List<Schedule> scheduleList = scheduleMapper.selectByDoctorId(doctorId);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Schedule schedule : scheduleList) {
            if (date != null && !schedule.getVisitDate().equals(date)) {
                continue;
            }
            
            Map<String, Object> item = new HashMap<>();
            item.put("id", schedule.getId());
            item.put("visitDate", schedule.getVisitDate());
            item.put("visitTime", schedule.getVisitTime());
            item.put("status", schedule.getStatus());
            
            List<NumberSource> numberSources = numberSourceMapper.selectByScheduleId(schedule.getId());
            List<Map<String, Object>> timeSlots = new ArrayList<>();
            
            for (NumberSource ns : numberSources) {
                Map<String, Object> slot = new HashMap<>();
                slot.put("id", ns.getId());
                slot.put("scheduleId", schedule.getId());
                slot.put("totalNum", ns.getTotalNum());
                slot.put("remainNum", ns.getRemainNum());
                slot.put("fee", ns.getFee());
                slot.put("status", ns.getStatus());
                timeSlots.add(slot);
            }
            
            item.put("timeSlots", timeSlots);
            result.add(item);
        }
        
        result.sort((a, b) -> {
            String dateA = (String) a.get("visitDate");
            String dateB = (String) b.get("visitDate");
            int dateCompare = dateA.compareTo(dateB);
            if (dateCompare != 0) {
                return dateCompare;
            }
            String timeA = (String) a.get("visitTime");
            String timeB = (String) b.get("visitTime");
            return getTimeOrder(timeA) - getTimeOrder(timeB);
        });
        
        return ResponseVo.success(result);
    }
    
    private int getTimeOrder(String time) {
        if (time == null) return 999;
        if (time.contains("08:00")) return 1;
        if (time.contains("10:00")) return 2;
        if (time.contains("14:00")) return 3;
        if (time.contains("16:00")) return 4;
        if (time.contains("上午")) return 5;
        if (time.contains("下午")) return 6;
        return 999;
    }

    @GetMapping("/numbersource/list")
    public ResponseVo getNumberSourceList(@RequestParam Integer scheduleId) {
        List<NumberSource> numberSourceList = numberSourceMapper.selectByScheduleId(scheduleId);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (NumberSource ns : numberSourceList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", ns.getId());
            item.put("scheduleId", ns.getScheduleId());
            item.put("totalNum", ns.getTotalNum());
            item.put("remainNum", ns.getRemainNum());
            item.put("fee", ns.getFee());
            item.put("status", ns.getStatus());
            
            result.add(item);
        }
        
        return ResponseVo.success(result);
    }
}
