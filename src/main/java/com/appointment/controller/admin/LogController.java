package com.appointment.controller.admin;

import com.appointment.entity.OperateLog;
import com.appointment.mapper.OperateLogMapper;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/log")
public class LogController {

    @Autowired
    private OperateLogMapper operateLogMapper;

    @GetMapping("/list")
    public ResponseVo list() {
        List<OperateLog> logs = operateLogMapper.selectAll();
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (OperateLog log : logs) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("id", log.getId());
            logMap.put("createTime", log.getOperateTime());
            logMap.put("username", getOperatorName(log.getOperatorId(), log.getOperatorRole()));
            logMap.put("operation", log.getContent());
            logMap.put("ip", log.getOperateIp());
            
            result.add(logMap);
        }

        return ResponseVo.success(result);
    }

    private String getOperatorName(Integer operatorId, Integer operatorRole) {
        if (operatorRole == 2) {
            return "管理员";
        } else if (operatorRole == 1) {
            return "医生";
        } else {
            return "用户";
        }
    }
}
