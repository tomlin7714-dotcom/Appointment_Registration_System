package com.appointment.controller.user;

import com.appointment.entity.User;
import com.appointment.mapper.UserMapper;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserInfoController {

    @Autowired
    private UserMapper userMapper;

    @Value("${file.upload.dir:}")
    private String uploadDir;

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

    @PostMapping("/avatar/upload")
    public ResponseVo uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseVo.error(400, "请选择要上传的文件");
        }

        try {
            // 使用项目根目录下的 uploads/avatars
            String baseDir = System.getProperty("user.dir");
            String saveDir = uploadDir.isEmpty() ? baseDir + "/uploads/avatars" : uploadDir;
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalName = file.getOriginalFilename();
            String ext = originalName != null ? originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
            String fileName = UUID.randomUUID().toString() + ext;

            // 保存文件
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            // 返回可访问的URL
            String avatarUrl = "/uploads/avatars/" + fileName;

            Map<String, Object> data = new HashMap<>();
            data.put("avatarUrl", avatarUrl);

            return ResponseVo.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVo.error(500, "头像上传失败: " + e.getMessage());
        }
    }
}
