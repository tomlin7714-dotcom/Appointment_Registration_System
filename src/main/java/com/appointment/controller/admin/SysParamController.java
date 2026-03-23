package com.appointment.controller.admin;

import com.appointment.entity.SysParam;
import com.appointment.vo.ResponseVo;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/sysParam")
public class SysParamController {

    // 模拟系统参数数据
    private static final List<SysParam> sysParamList = new ArrayList<>();
    static {
        SysParam param1 = new SysParam();
        param1.setId(1);
        param1.setParamName("wechat.msg.switch");
        param1.setParamValue("1");
        param1.setParamDesc("微信消息推送开关");
        param1.setModifyTime(new Date());

        SysParam param2 = new SysParam();
        param2.setId(2);
        param2.setParamName("visit.remind.time");
        param2.setParamValue("120");
        param2.setParamDesc("就诊提醒提前时间（分钟）");
        param2.setModifyTime(new Date());

        SysParam param3 = new SysParam();
        param3.setId(3);
        param3.setParamName("login.fail.lock.time");
        param3.setParamValue("15");
        param3.setParamDesc("登录失败锁定时间（分钟）");
        param3.setModifyTime(new Date());

        sysParamList.add(param1);
        sysParamList.add(param2);
        sysParamList.add(param3);
    }

    @GetMapping("/list")
    public ResponseVo list() {
        return ResponseVo.success(sysParamList);
    }

    @PutMapping("/update")
    public ResponseVo update(@RequestBody SysParam param) {
        if (param.getId() == null || param.getParamValue() == null) {
            return ResponseVo.error(400, "参数错误");
        }

        for (SysParam p : sysParamList) {
            if (p.getId().equals(param.getId())) {
                p.setParamValue(param.getParamValue());
                p.setModifyTime(new Date());
                return ResponseVo.success();
            }
        }

        return ResponseVo.error(404, "参数不存在");
    }

    @GetMapping("/status")
    public ResponseVo getSystemStatus() {
        // 模拟系统状态数据
        Map<String, Object> status = new HashMap<>();
        status.put("cpuUsage", "30%");
        status.put("memoryUsage", "45%");
        status.put("dbConnections", 10);
        status.put("onlineUsers", 20);
        status.put("todayAppointments", 50);
        status.put("todayPayments", 45);

        return ResponseVo.success(status);
    }
}