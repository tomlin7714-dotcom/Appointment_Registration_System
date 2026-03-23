package com.appointment.controller.doctor;

import com.appointment.entity.Dept;
import com.appointment.entity.Doctor;
import com.appointment.mapper.DeptMapper;
import com.appointment.mapper.DoctorMapper;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class DoctorLoginController {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DeptMapper deptMapper;

    @PostMapping("/login")
    public ResponseVo login(@RequestBody Map<String, String> params) {
        String doctorAccount = params.get("doctorAccount");
        String pwd = params.get("pwd");

        if (doctorAccount == null || pwd == null) {
            return ResponseVo.error(400, "参数错误");
        }

        Doctor doctor = doctorMapper.selectByWorkNo(doctorAccount);
        if (doctor == null) {
            return ResponseVo.error(401, "工号不存在");
        }

        if (!doctor.getPwd().equals(pwd)) {
            return ResponseVo.error(401, "密码错误");
        }

        if (doctor.getStatus() != 0) {
            return ResponseVo.error(401, "账号已被禁用");
        }

        doctorMapper.updateLastLoginTime(doctor.getId());

        Dept dept = deptMapper.selectById(doctor.getDeptId());
        
        Map<String, Object> doctorInfo = new HashMap<>();
        doctorInfo.put("id", doctor.getId());
        doctorInfo.put("doctorAccount", doctor.getWorkNo());
        doctorInfo.put("name", doctor.getName());
        doctorInfo.put("title", doctor.getTitle());
        doctorInfo.put("phone", doctor.getPhone());
        doctorInfo.put("remark", doctor.getIntro());
        doctorInfo.put("specialty", doctor.getSpecialty());
        doctorInfo.put("deptId", doctor.getDeptId());
        doctorInfo.put("deptName", dept != null ? dept.getDeptName() : "");

        Map<String, Object> data = new HashMap<>();
        data.put("doctorId", doctor.getId());
        data.put("token", "doctor_token_" + doctor.getId());
        data.put("doctorInfo", doctorInfo);

        return ResponseVo.success(data);
    }

    @PostMapping("/pwd/modify")
    public ResponseVo modifyPassword(@RequestBody Map<String, String> params) {
        String oldPwd = params.get("oldPwd");
        String newPwd = params.get("newPwd");
        String workNo = params.get("workNo");

        if (oldPwd == null || newPwd == null || workNo == null) {
            return ResponseVo.error(400, "参数错误");
        }

        Doctor doctor = doctorMapper.selectByWorkNo(workNo);
        if (doctor == null) {
            return ResponseVo.error(404, "医生不存在");
        }

        if (!doctor.getPwd().equals(oldPwd)) {
            return ResponseVo.error(400, "旧密码错误");
        }

        doctor.setPwd(newPwd);
        doctorMapper.update(doctor);

        return ResponseVo.success();
    }
}
