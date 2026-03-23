package com.appointment.controller;

import com.appointment.entity.Admin;
import com.appointment.entity.Dept;
import com.appointment.entity.Doctor;
import com.appointment.entity.User;
import com.appointment.mapper.AdminMapper;
import com.appointment.mapper.DeptMapper;
import com.appointment.mapper.DoctorMapper;
import com.appointment.mapper.UserMapper;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DeptMapper deptMapper;

    @GetMapping("/health")
    public ResponseVo health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "ok");
        data.put("message", "医疗预约系统运行正常");
        return ResponseVo.success(data);
    }

    @GetMapping("/db")
    public ResponseVo testDatabase() {
        Map<String, Object> data = new HashMap<>();
        
        try {
            Admin admin = adminMapper.selectByAccount("admin");
            data.put("admin", admin);
            
            List<Dept> depts = deptMapper.selectAll();
            data.put("deptCount", depts.size());
            data.put("depts", depts);
            
            List<Doctor> doctors = doctorMapper.selectAll();
            data.put("doctorCount", doctors.size());
            data.put("doctors", doctors);
            
            List<User> users = userMapper.selectAll();
            data.put("userCount", users.size());
            data.put("users", users);
            
            data.put("dbStatus", "connected");
        } catch (Exception e) {
            data.put("dbStatus", "error");
            data.put("error", e.getMessage());
        }
        
        return ResponseVo.success(data);
    }

    @GetMapping("/insert")
    public ResponseVo testInsert() {
        Map<String, Object> data = new HashMap<>();
        
        try {
            User user = new User();
            user.setPhone("13900000000");
            user.setPwd("test123");
            user.setRealName("测试用户");
            user.setIdCard("410101199001011111");
            user.setStatus(0);
            
            int result = userMapper.insert(user);
            data.put("insertResult", result);
            data.put("message", "插入成功");
        } catch (Exception e) {
            data.put("message", "插入失败: " + e.getMessage());
        }
        
        return ResponseVo.success(data);
    }
}
