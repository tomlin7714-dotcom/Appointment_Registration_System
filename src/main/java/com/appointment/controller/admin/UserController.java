package com.appointment.controller.admin;

import com.appointment.entity.User;
import com.appointment.entity.OperateLog;
import com.appointment.mapper.UserMapper;
import com.appointment.mapper.OperateLogMapper;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OperateLogMapper operateLogMapper;

    @GetMapping("/list")
    public ResponseVo list(@RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Integer status) {
        List<User> users = userMapper.selectAll();
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (User user : users) {
            boolean match = true;
            
            if (keyword != null && !keyword.isEmpty()) {
                if (!user.getRealName().contains(keyword) && !user.getPhone().contains(keyword)) {
                    match = false;
                }
            }
            if (status != null && !user.getStatus().equals(status)) {
                match = false;
            }
            
            if (match) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("name", user.getRealName());
                userMap.put("phone", user.getPhone());
                userMap.put("idCard", user.getIdCard());
                userMap.put("status", user.getStatus());
                userMap.put("createTime", user.getCreateTime());
                
                result.add(userMap);
            }
        }

        return ResponseVo.success(result);
    }

    @GetMapping("/status")
    public ResponseVo updateStatus(@RequestParam Integer id, @RequestParam Integer status) {
        if (id == null) {
            return ResponseVo.error(400, "用户ID不能为空");
        }

        User user = userMapper.selectById(id);
        if (user == null) {
            return ResponseVo.error(404, "用户不存在");
        }

        user.setStatus(status);
        userMapper.update(user);

        OperateLog log = new OperateLog();
        log.setOperatorId(1);
        log.setOperatorRole(2);
        log.setContent((status == 0 ? "启用" : "禁用") + "用户：" + user.getRealName());
        log.setOperateIp("127.0.0.1");
        operateLogMapper.insert(log);

        return ResponseVo.success();
    }
}
