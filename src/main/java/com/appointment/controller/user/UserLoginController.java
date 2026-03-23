package com.appointment.controller.user;

import com.appointment.entity.User;
import com.appointment.mapper.UserMapper;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserLoginController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register")
    public ResponseVo register(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String pwd = params.get("pwd");
        String realName = params.get("realName");
        String idCard = params.get("idCard");

        if (phone == null || pwd == null || realName == null || idCard == null) {
            return ResponseVo.error(400, "参数错误");
        }

        User existUser = userMapper.selectByPhone(phone);
        if (existUser != null) {
            return ResponseVo.error(400, "手机号已注册");
        }

        User user = new User();
        user.setPhone(phone);
        user.setPwd(pwd);
        user.setRealName(realName);
        user.setIdCard(idCard);
        user.setStatus(0);

        userMapper.insert(user);

        return ResponseVo.success();
    }

    @PostMapping("/login")
    public ResponseVo login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String pwd = params.get("pwd");

        if (phone == null || pwd == null) {
            return ResponseVo.error(400, "参数错误");
        }

        User user = userMapper.selectByPhone(phone);
        if (user == null) {
            return ResponseVo.error(401, "手机号未注册");
        }

        if (!user.getPwd().equals(pwd)) {
            return ResponseVo.error(401, "密码错误");
        }

        if (user.getStatus() != 0) {
            return ResponseVo.error(401, "账号已被禁用");
        }

        userMapper.updateLastLoginTime(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("token", "user_token_" + user.getId());
        data.put("userInfo", user);

        return ResponseVo.success(data);
    }

    @PostMapping("/pwd/modify")
    public ResponseVo modifyPassword(@RequestBody Map<String, String> params) {
        String oldPwd = params.get("oldPwd");
        String newPwd = params.get("newPwd");
        String phone = params.get("phone");

        if (oldPwd == null || newPwd == null || phone == null) {
            return ResponseVo.error(400, "参数错误");
        }

        User user = userMapper.selectByPhone(phone);
        if (user == null) {
            return ResponseVo.error(404, "用户不存在");
        }

        if (!user.getPwd().equals(oldPwd)) {
            return ResponseVo.error(400, "旧密码错误");
        }

        user.setPwd(newPwd);
        userMapper.update(user);

        return ResponseVo.success();
    }
}
