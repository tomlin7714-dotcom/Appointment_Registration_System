package com.appointment.controller.admin;

import com.appointment.entity.ConsultationForm;
import com.appointment.mapper.ConsultationFormMapper;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/admin/consultation")
public class ConsultationManageController {

    @Autowired
    private ConsultationFormMapper consultationFormMapper;

    @GetMapping("/list")
    public ResponseVo list(@RequestParam(required = false) String doctorName,
                           @RequestParam(required = false) String startTime,
                           @RequestParam(required = false) String endTime,
                           @RequestParam(required = false, defaultValue = "1") Integer page,
                           @RequestParam(required = false, defaultValue = "9") Integer pageSize) {
        List<Map<String, Object>> result = new ArrayList<>();
        // 获取所有问诊单
        consultationFormMapper.selectAll().forEach(cf -> result.add(toMap(cf)));

        // 筛选：医生姓名
        if (doctorName != null && !doctorName.isEmpty()) {
            result.removeIf(item -> {
                String name = (String) item.get("doctorName");
                return name == null || !name.contains(doctorName);
            });
        }

        // 筛选：创建时间范围
        if (startTime != null && !startTime.isEmpty()) {
            result.removeIf(item -> {
                String ct = (String) item.get("createTime");
                return ct == null || ct.compareTo(startTime) < 0;
            });
        }
        if (endTime != null && !endTime.isEmpty()) {
            result.removeIf(item -> {
                String ct = (String) item.get("createTime");
                return ct == null || ct.compareTo(endTime + " 23:59:59") > 0;
            });
        }

        // 按创建时间降序
        result.sort((a, b) -> {
            String ta = (String) a.get("createTime");
            String tb = (String) b.get("createTime");
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });

        // 分页
        int total = result.size();
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<Map<String, Object>> pageList = start < total ? result.subList(start, end) : new ArrayList<>();

        Map<String, Object> data = new HashMap<>();
        data.put("list", pageList);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);

        return ResponseVo.success(data);
    }

    @GetMapping("/get")
    public ResponseVo get(@RequestParam Integer id) {
        if (id == null) return ResponseVo.error(400, "ID不能为空");
        ConsultationForm cf = consultationFormMapper.selectById(id);
        if (cf == null) return ResponseVo.error(404, "问诊单不存在");
        return ResponseVo.success(toMap(cf));
    }

    @PostMapping("/update")
    public ResponseVo update(@RequestBody Map<String, Object> params) {
        Integer id = parseInteger(params.get("id"));
        if (id == null) return ResponseVo.error(400, "ID不能为空");

        ConsultationForm cf = consultationFormMapper.selectById(id);
        if (cf == null) return ResponseVo.error(404, "问诊单不存在");

        String diagnosis = (String) params.get("diagnosis");
        String treatment = (String) params.get("treatment");
        String examination = (String) params.get("examination");

        if (diagnosis != null) cf.setDiagnosis(diagnosis);
        if (treatment != null) cf.setTreatment(treatment);
        if (examination != null) cf.setExamination(examination);

        consultationFormMapper.update(cf);
        return ResponseVo.success();
    }

    @PostMapping("/status")
    public ResponseVo updateStatus(@RequestBody Map<String, Object> params) {
        Integer id = parseInteger(params.get("id"));
        Integer status = parseInteger(params.get("status"));
        if (id == null || status == null) return ResponseVo.error(400, "参数错误");

        ConsultationForm cf = consultationFormMapper.selectById(id);
        if (cf == null) return ResponseVo.error(404, "问诊单不存在");

        cf.setStatus(status);
        consultationFormMapper.update(cf);
        return ResponseVo.success();
    }

    @GetMapping("/del")
    public ResponseVo delete(@RequestParam Integer id) {
        ConsultationForm cf = consultationFormMapper.selectById(id);
        if (cf == null) return ResponseVo.error(404, "问诊单不存在");
        consultationFormMapper.deleteById(id);
        return ResponseVo.success();
    }

    private Map<String, Object> toMap(ConsultationForm cf) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", cf.getId());
        m.put("consultationNo", cf.getConsultationNo());
        m.put("patientName", cf.getPatientName());
        m.put("doctorName", cf.getDoctorName());
        m.put("diagnosis", cf.getDiagnosis());
        m.put("treatment", cf.getTreatment());
        m.put("examination", cf.getExamination());
        m.put("status", cf.getStatus());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        m.put("createTime", cf.getCreateTime() != null ? sdf.format(cf.getCreateTime()) : "");
        return m;
    }

    private Integer parseInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof String) try { return Integer.parseInt((String) obj); } catch (Exception e) { return null; }
        if (obj instanceof Number) return ((Number) obj).intValue();
        return null;
    }
}
