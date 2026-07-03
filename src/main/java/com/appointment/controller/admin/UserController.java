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
                          @RequestParam(required = false) Integer status,
                          @RequestParam(required = false, defaultValue = "1") Integer page,
                          @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        List<User> users = userMapper.selectAll();
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (User user : users) {
            boolean match = true;
            
            if (keyword != null && !keyword.isEmpty()) {
                boolean nameMatch = user.getRealName().contains(keyword);
                boolean phoneMatch = user.getPhone() != null && user.getPhone().contains(keyword);
                if (!nameMatch && !phoneMatch) {
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
        
        // 按ID升序排列
        result.sort((a, b) -> {
            Integer idA = (Integer) a.get("id");
            Integer idB = (Integer) b.get("id");
            return idA.compareTo(idB);
        });
        
        // 分页处理
        int total = result.size();
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<Map<String, Object>> paginatedResult = start < total ? result.subList(start, end) : new ArrayList<>();
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("list", paginatedResult);
        responseData.put("total", total);
        responseData.put("page", page);
        responseData.put("pageSize", pageSize);

        return ResponseVo.success(responseData);
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
