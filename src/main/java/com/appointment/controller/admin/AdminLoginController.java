package com.appointment.controller.admin;

import com.appointment.entity.Admin;
import com.appointment.mapper.AdminMapper;
import com.appointment.util.JwtUtil;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {

    @Autowired
    private AdminMapper adminMapper;

    @PostMapping("/login")
    public ResponseVo login(@RequestBody Map<String, String> params) {
        String adminAccount = params.get("adminAccount");
        String pwd = params.get("pwd");

        if (adminAccount == null || pwd == null) {
            return ResponseVo.error(400, "参数错误");
        }

        Admin admin = adminMapper.selectByAccount(adminAccount);
        if (admin == null) {
            return ResponseVo.error(401, "账号不存在");
        }

        if (!admin.getPwd().equals(pwd)) {
            return ResponseVo.error(401, "密码错误");
        }

        adminMapper.updateLastLoginTime(admin.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("adminId", admin.getId());
        data.put("token", "admin_token_" + admin.getId());
        data.put("adminInfo", admin);

        return ResponseVo.success(data);
    }

    @PostMapping("/pwd/modify")
    public ResponseVo modifyPassword(@RequestBody Map<String, String> params) {
        String oldPwd = params.get("oldPwd");
        String newPwd = params.get("newPwd");
        String adminAccount = params.get("adminAccount");

        if (oldPwd == null || newPwd == null || adminAccount == null) {
            return ResponseVo.error(400, "参数错误");
        }

        Admin admin = adminMapper.selectByAccount(adminAccount);
        if (admin == null) {
            return ResponseVo.error(404, "管理员不存在");
        }

        if (!admin.getPwd().equals(oldPwd)) {
            return ResponseVo.error(400, "旧密码错误");
        }

        admin.setPwd(newPwd);
        adminMapper.update(admin);

        return ResponseVo.success();
    }
}
