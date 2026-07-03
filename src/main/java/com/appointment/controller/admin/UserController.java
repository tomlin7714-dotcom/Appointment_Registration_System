package com.appointment.controller.admin;

import com.appointment.entity.User;
import com.appointment.entity.OperateLog;
import com.appointment.mapper.UserMapper;
import com.appointment.mapper.OperateLogMapper;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OperateLogMapper operateLogMapper;

    @GetMapping("/list")
    public ResponseVo list(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String phone,
                           @RequestParam(required = false) String startTime,
                           @RequestParam(required = false) String endTime,
                           @RequestParam(required = false) Integer status,
                           @RequestParam(required = false, defaultValue = "1") Integer page,
                           @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        List<User> users = userMapper.selectAll();

        List<Map<String, Object>> result = new ArrayList<>();

        for (User user : users) {
            boolean match = true;

            // 姓名/手机模糊搜索
            if (keyword != null && !keyword.isEmpty()) {
                boolean nameMatch = user.getRealName() != null && user.getRealName().contains(keyword);
                boolean phoneMatch = user.getPhone() != null && user.getPhone().contains(keyword);
                if (!nameMatch && !phoneMatch) match = false;
            }

            // 手机号精确搜索
            if (phone != null && !phone.isEmpty()) {
                if (user.getPhone() == null || !user.getPhone().contains(phone)) match = false;
            }

            // 状态筛选
            if (status != null && !user.getStatus().equals(status)) match = false;

            // 注册时间范围
            if (startTime != null && !startTime.isEmpty()) {
                if (user.getCreateTime() == null) match = false;
                else {
                    String ct = user.getCreateTime().toString();
                    if (ct.compareTo(startTime) < 0) match = false;
                }
            }
            if (endTime != null && !endTime.isEmpty()) {
                if (user.getCreateTime() == null) match = false;
                else {
                    String ct = user.getCreateTime().toString();
                    if (ct.compareTo(endTime + " 23:59:59") > 0) match = false;
                }
            }

            if (match) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("name", user.getRealName());
                userMap.put("phone", user.getPhone());
                userMap.put("idCard", user.getIdCard());
                userMap.put("avatar", user.getAvatar());
                userMap.put("status", user.getStatus());
                userMap.put("userRole", "普通用户");
                userMap.put("createTime", user.getCreateTime());
                userMap.put("lastLoginTime", user.getLastLoginTime());
                result.add(userMap);
            }
        }

        // 按ID降序
        result.sort((a, b) -> {
            Integer idA = (Integer) a.get("id");
            Integer idB = (Integer) b.get("id");
            return idB.compareTo(idA);
        });

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

    @GetMapping("/get")
    public ResponseVo get(@RequestParam Integer id) {
        if (id == null) return ResponseVo.error(400, "用户ID不能为空");
        User user = userMapper.selectById(id);
        if (user == null) return ResponseVo.error(404, "用户不存在");

        Map<String, Object> m = new HashMap<>();
        m.put("id", user.getId());
        m.put("name", user.getRealName());
        m.put("phone", user.getPhone());
        m.put("idCard", user.getIdCard());
        m.put("pwd", user.getPwd());
        m.put("status", user.getStatus());
        return ResponseVo.success(m);
    }

    @PostMapping("/save")
    public ResponseVo save(@RequestBody Map<String, Object> params) {
        String name = (String) params.get("name");
        String phone = (String) params.get("phone");
        String pwd = (String) params.get("pwd");
        String idCard = (String) params.get("idCard");

        if (name == null || phone == null || pwd == null) {
            return ResponseVo.error(400, "姓名、手机号、密码不能为空");
        }

        User exist = userMapper.selectByPhone(phone);
        if (exist != null) return ResponseVo.error(400, "手机号已存在");

        User user = new User();
        user.setRealName(name);
        user.setPhone(phone);
        user.setPwd(pwd);
        user.setIdCard(idCard != null ? idCard : "");
        user.setStatus(0);
        userMapper.insert(user);

        OperateLog log = new OperateLog();
        log.setOperatorId(1);
        log.setOperatorRole(2);
        log.setContent("新增用户：" + name + "(" + phone + ")");
        log.setOperateIp("127.0.0.1");
        operateLogMapper.insert(log);

        return ResponseVo.success();
    }

    @PostMapping("/modify")
    public ResponseVo modify(@RequestBody Map<String, Object> params) {
        Integer id = parseInteger(params.get("id"));
        if (id == null) return ResponseVo.error(400, "用户ID不能为空");

        User user = userMapper.selectById(id);
        if (user == null) return ResponseVo.error(404, "用户不存在");

        String name = (String) params.get("name");
        String pwd = (String) params.get("pwd");
        String idCard = (String) params.get("idCard");
        Integer status = parseInteger(params.get("status"));

        if (name != null && !name.isEmpty()) user.setRealName(name);
        if (pwd != null && !pwd.isEmpty()) user.setPwd(pwd);
        if (idCard != null) user.setIdCard(idCard);
        if (status != null) user.setStatus(status);

        userMapper.update(user);

        OperateLog log = new OperateLog();
        log.setOperatorId(1);
        log.setOperatorRole(2);
        log.setContent("修改用户信息：" + user.getRealName());
        log.setOperateIp("127.0.0.1");
        operateLogMapper.insert(log);

        return ResponseVo.success();
    }

    @GetMapping("/del")
    public ResponseVo delete(@RequestParam Integer id) {
        if (id == null) return ResponseVo.error(400, "用户ID不能为空");
        User user = userMapper.selectById(id);
        if (user == null) return ResponseVo.error(404, "用户不存在");

        userMapper.delete(id);

        OperateLog log = new OperateLog();
        log.setOperatorId(1);
        log.setOperatorRole(2);
        log.setContent("删除用户：" + user.getRealName());
        log.setOperateIp("127.0.0.1");
        operateLogMapper.insert(log);

        return ResponseVo.success();
    }

    @GetMapping("/status")
    public ResponseVo updateStatus(@RequestParam Integer id, @RequestParam Integer status) {
        if (id == null) return ResponseVo.error(400, "用户ID不能为空");
        User user = userMapper.selectById(id);
        if (user == null) return ResponseVo.error(404, "用户不存在");
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

    private Integer parseInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof String) try { return Integer.parseInt((String) obj); } catch (Exception e) { return null; }
        if (obj instanceof Number) return ((Number) obj).intValue();
        return null;
    }
}
