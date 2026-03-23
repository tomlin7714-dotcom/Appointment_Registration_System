package com.appointment.controller.admin;

import com.appointment.entity.Dept;
import com.appointment.entity.OperateLog;
import com.appointment.mapper.DeptMapper;
import com.appointment.mapper.OperateLogMapper;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dept")
public class DeptController {

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private OperateLogMapper operateLogMapper;

    @GetMapping("/list")
    public ResponseVo list() {
        List<Dept> depts = deptMapper.selectAll();
        
        for (Dept dept : depts) {
            Map<String, Object> deptMap = new HashMap<>();
            deptMap.put("id", dept.getId());
            deptMap.put("name", dept.getDeptName());
            deptMap.put("remark", dept.getDeptDesc());
            deptMap.put("createTime", dept.getCreateTime());
        }
        
        return ResponseVo.success(depts);
    }

    @PostMapping("/save")
    public ResponseVo save(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        String remark = params.get("remark");

        if (name == null || name.isEmpty()) {
            return ResponseVo.error(400, "科室名称不能为空");
        }

        Dept dept = new Dept();
        dept.setDeptName(name);
        dept.setDeptDesc(remark);
        dept.setStatus(0);

        deptMapper.insert(dept);

        Map<String, Object> data = new HashMap<>();
        data.put("id", dept.getId());

        OperateLog log = new OperateLog();
        log.setOperatorId(1);
        log.setOperatorRole(2);
        log.setContent("新增科室：" + name);
        log.setOperateIp("127.0.0.1");
        operateLogMapper.insert(log);

        return ResponseVo.success(data);
    }

    @PostMapping("/modify")
    public ResponseVo modify(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        String name = (String) params.get("name");
        String remark = (String) params.get("remark");

        if (id == null) {
            return ResponseVo.error(400, "科室ID不能为空");
        }

        Dept dept = deptMapper.selectById(id);
        if (dept == null) {
            return ResponseVo.error(404, "科室不存在");
        }

        if (name != null && !name.isEmpty()) {
            dept.setDeptName(name);
        }
        if (remark != null) {
            dept.setDeptDesc(remark);
        }

        deptMapper.update(dept);

        return ResponseVo.success();
    }

    @GetMapping("/del")
    public ResponseVo delete(@RequestParam Integer id) {
        if (id == null) {
            return ResponseVo.error(400, "科室ID不能为空");
        }

        Dept dept = deptMapper.selectById(id);
        if (dept == null) {
            return ResponseVo.error(404, "科室不存在");
        }

        String deptName = dept.getDeptName();
        deptMapper.delete(id);

        OperateLog log = new OperateLog();
        log.setOperatorId(1);
        log.setOperatorRole(2);
        log.setContent("删除科室：" + deptName);
        log.setOperateIp("127.0.0.1");
        operateLogMapper.insert(log);

        return ResponseVo.success();
    }
}
