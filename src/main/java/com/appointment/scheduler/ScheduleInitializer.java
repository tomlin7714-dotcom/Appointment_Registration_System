package com.appointment.scheduler;

import com.appointment.entity.Doctor;
import com.appointment.entity.Schedule;
import com.appointment.entity.NumberSource;
import com.appointment.entity.Appointment;
import com.appointment.mapper.DoctorMapper;
import com.appointment.mapper.ScheduleMapper;
import com.appointment.mapper.NumberSourceMapper;
import com.appointment.mapper.AppointmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ScheduleInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleInitializer.class);

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private NumberSourceMapper numberSourceMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("========== 系统启动完成，开始初始化排班 ==========");
        
        try {
            cleanAllSchedules();
            generateNewSchedules();
            
            logger.info("========== 排班初始化完成 ==========");
        } catch (Exception e) {
            logger.error("排班初始化失败: " + e.getMessage(), e);
        }
    }

    private void cleanAllSchedules() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        
        List<Schedule> allSchedules = scheduleMapper.selectAll();
        int deletedCount = 0;
        
        for (Schedule schedule : allSchedules) {
            // 只清理过期的排班（昨天及之前）
            if (schedule.getVisitDate().compareTo(today) < 0) {
                List<NumberSource> numberSources = numberSourceMapper.selectByScheduleId(schedule.getId());
                
                for (NumberSource ns : numberSources) {
                    // 只删除号源，保留预约记录
                    numberSourceMapper.delete(ns.getId());
                }
                
                scheduleMapper.delete(schedule.getId());
                deletedCount++;
            }
        }
        
        logger.info("清理过期排班完成，删除 {} 个排班", deletedCount);
    }

    private void generateNewSchedules() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        
        String[] periods = {
            "上午 08:00-10:00",
            "上午 10:00-12:00",
            "下午 14:00-16:00",
            "下午 16:00-18:00"
        };
        
        List<Doctor> doctors = doctorMapper.selectAll();
        int totalCreated = 0;
        
        for (Doctor doctor : doctors) {
            int doctorCreated = 0;
            
            int restDay1 = (doctor.getId() % 7) + 1;
            int restDay2 = ((doctor.getId() + 1) % 7) + 1;
            
            for (int i = 0; i <= 14; i++) {
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_MONTH, i);
                String visitDate = sdf.format(cal.getTime());
                
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                
                if (dayOfWeek == restDay1 || dayOfWeek == restDay2) {
                    continue;
                }
                
                for (String period : periods) {
                    List<Schedule> existing = scheduleMapper.selectByDoctorIdAndDate(doctor.getId(), visitDate);
                    boolean hasPeriod = false;
                    for (Schedule s : existing) {
                        if (period.equals(s.getVisitTime())) {
                            hasPeriod = true;
                            break;
                        }
                    }
                    
                    if (!hasPeriod) {
                        Schedule schedule = new Schedule();
                        schedule.setDoctorId(doctor.getId());
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
                        
                        doctorCreated++;
                    }
                }
            }
            
            totalCreated += doctorCreated;
            if (doctorCreated > 0) {
                logger.info("医生[{}]自动排班完成，创建 {} 个班次，休息日: 周{}、周{}", 
                    doctor.getName(), doctorCreated, getDayName(restDay1), getDayName(restDay2));
            }
        }
        
        logger.info("所有医生自动排班完成，共创建 {} 个班次", totalCreated);
    }
    
    private String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY: return "日";
            case Calendar.MONDAY: return "一";
            case Calendar.TUESDAY: return "二";
            case Calendar.WEDNESDAY: return "三";
            case Calendar.THURSDAY: return "四";
            case Calendar.FRIDAY: return "五";
            case Calendar.SATURDAY: return "六";
            default: return "";
        }
    }
}
