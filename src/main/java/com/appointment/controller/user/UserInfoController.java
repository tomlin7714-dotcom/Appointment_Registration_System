package com.appointment.controller.user;

import com.appointment.entity.User;
import com.appointment.mapper.UserMapper;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserInfoController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/info")
    public ResponseVo getInfo(@RequestParam Integer userId) {
        if (userId == null) {
            return ResponseVo.error(400, "用户ID不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return ResponseVo.error(404, "用户不存在");
        }

        user.setPwd(null);
        return ResponseVo.success(user);
    }

    @PostMapping("/info/update")
    public ResponseVo updateInfo(@RequestBody User user) {
        if (user.getId() == null) {
            return ResponseVo.error(400, "用户ID不能为空");
        }

        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            return ResponseVo.error(404, "用户不存在");
        }

        if (user.getRealName() != null) {
            existingUser.setRealName(user.getRealName());
        }
        if (user.getIdCard() != null) {
            existingUser.setIdCard(user.getIdCard());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }
        if (user.getAvatar() != null) {
            existingUser.setAvatar(user.getAvatar());
        }

        userMapper.update(existingUser);

        return ResponseVo.success();
    }
}
