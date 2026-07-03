package com.appointment.controller;

import com.appointment.entity.Dept;
import com.appointment.mapper.DeptMapper;
import com.appointment.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
public class AiTriageController {

    @Autowired
    private DeptMapper deptMapper;

    @Value("${ai.deepseek.apiKey:}")
    private String apiKey;

    @Value("${ai.deepseek.apiUrl:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    @PostMapping("/triage")
    public ResponseVo triage(@RequestBody Map<String, String> params) {
        String symptom = params.get("symptom");
        if (symptom == null || symptom.trim().isEmpty()) {
            return ResponseVo.error(400, "请输入症状描述");
        }

        try {
            // 获取所有科室列表供AI参考
            List<Dept> allDepts = deptMapper.selectAll();
            StringBuilder deptNames = new StringBuilder();
            for (Dept d : allDepts) {
                deptNames.append(d.getDeptName()).append("、");
            }
            if (deptNames.length() > 0) deptNames.setLength(deptNames.length() - 1);

            // 调用 DeepSeek API
            String aiResponse = callDeepSeek(symptom, deptNames.toString());

            // 解析 AI 回复，提取推荐科室和文字建议
            Map<String, Object> result = parseAiResponse(aiResponse, allDepts);

            return ResponseVo.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            // 降级处理：根据关键词匹配科室
            Map<String, Object> fallback = keywordMatch(symptom);
            return ResponseVo.success(fallback);
        }
    }

    private String callDeepSeek(String symptom, String deptList) throws Exception {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API Key 未配置，使用关键词匹配模式");
        }

        RestTemplate restTemplate = new RestTemplate();

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 1000);

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content",
            "你是一个专业的医疗导诊助手。请根据用户的症状描述，推荐合适的就诊科室，并给出简要建议。\n" +
            "可用科室有：" + deptList + "\n" +
            "请按以下JSON格式回复（不要包含其他内容）：\n" +
            "{\"depts\":[{\"name\":\"科室名称\"}],\"advice\":\"就医建议\"}\n" +
            "depts数组推荐1-3个最相关的科室，按优先级排序。");
        messages.add(sysMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", symptom);
        messages.add(userMsg);
        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

        if (response.getBody() != null) {
            List<Map> choices = (List<Map>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map message = (Map) choices.get(0).get("message");
                return (String) message.get("content");
            }
        }
        throw new RuntimeException("AI API 返回异常");
    }

    private Map<String, Object> parseAiResponse(String aiResponse, List<Dept> allDepts) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 尝试解析 JSON
            String jsonStr = aiResponse.trim();
            if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.replaceAll("```json|```", "").trim();
            }

            com.fasterxml.jackson.databind.ObjectMapper mapper =
                new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> parsed = mapper.readValue(jsonStr, Map.class);

            String advice = (String) parsed.getOrDefault("advice", "请根据推荐科室选择合适的医生进行挂号就诊。");
            result.put("advice", advice);

            List<Map<String, String>> deptNames = (List<Map<String, String>>) parsed.get("depts");
            List<Map<String, Object>> deptResult = new ArrayList<>();

            if (deptNames != null) {
                for (Map<String, String> d : deptNames) {
                    String name = d.get("name");
                    for (Dept dept : allDepts) {
                        if (dept.getDeptName().contains(name) || name.contains(dept.getDeptName())) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("deptId", dept.getId());
                            item.put("deptName", dept.getDeptName());
                            deptResult.add(item);
                            break;
                        }
                    }
                    // 未匹配到已知科室也展示
                    if (deptResult.stream().noneMatch(item -> item.get("deptName").equals(name))) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("deptId", null);
                        item.put("deptName", name);
                        deptResult.add(item);
                    }
                }
            }

            result.put("depts", deptResult);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("advice", aiResponse);
            result.put("depts", Collections.emptyList());
        }

        return result;
    }

    private Map<String, Object> keywordMatch(String symptom) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> depts = new ArrayList<>();
        List<Dept> allDepts = deptMapper.selectAll();

        // 关键词匹配规则
        Map<String, String[]> rules = new LinkedHashMap<>();
        rules.put("内科", new String[]{"头痛", "发热", "感冒", "咳嗽", "胸闷", "心慌", "高血压", "糖尿病", "胃痛", "腹痛", "腹泻"});
        rules.put("外科", new String[]{"骨折", "外伤", "伤口", "阑尾", "肿块", "痔疮"});
        rules.put("儿科", new String[]{"小儿", "儿童", "婴儿", "宝宝", "孩子发烧", "幼儿"});
        rules.put("妇产科", new String[]{"怀孕", "月经", "妇科", "白带", "产后", "备孕"});
        rules.put("眼科", new String[]{"眼睛", "视力", "近视", "眼红", "眼花", "流泪"});
        rules.put("皮肤科", new String[]{"皮肤", "过敏", "疹子", "痘痘", "癣", "痒"});
        rules.put("骨科", new String[]{"腰疼", "腿疼", "颈椎", "关节", "背痛", "腰椎"});
        rules.put("耳鼻喉科", new String[]{"耳朵", "鼻塞", "喉咙", "咽炎", "中耳炎", "鼻炎"});
        rules.put("口腔科", new String[]{"牙疼", "牙龈", "口腔溃疡", "牙齿"});
        rules.put("神经内科", new String[]{"头晕", "眩晕", "失眠", "面瘫", "中风", "癫痫"});
        rules.put("心血管内科", new String[]{"心绞痛", "冠心病", "心律失常", "高血压"});
        rules.put("呼吸内科", new String[]{"肺炎", "支气管", "哮喘", "呼吸困难"});
        rules.put("消化内科", new String[]{"胃炎", "胃溃疡", "便秘", "消化不良", "胆囊"});
        rules.put("泌尿外科", new String[]{"肾结石", "前列腺", "尿频", "尿痛"});
        rules.put("急诊科", new String[]{"急救", "中毒", "大出血", "昏迷", "车祸"});

        Set<Integer> matchedIds = new LinkedHashSet<>();
        for (Map.Entry<String, String[]> entry : rules.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (symptom.contains(keyword)) {
                    for (Dept dept : allDepts) {
                        if (dept.getDeptName().equals(entry.getKey())) {
                            matchedIds.add(dept.getId());
                            break;
                        }
                    }
                    break;
                }
            }
        }

        for (Integer id : matchedIds) {
            for (Dept dept : allDepts) {
                if (dept.getId().equals(id)) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("deptId", dept.getId());
                    item.put("deptName", dept.getDeptName());
                    depts.add(item);
                    break;
                }
            }
        }

        if (depts.isEmpty()) {
            // 默认推荐
            for (Dept dept : allDepts) {
                Map<String, Object> item = new HashMap<>();
                item.put("deptId", dept.getId());
                item.put("deptName", dept.getDeptName());
                depts.add(item);
                if (depts.size() >= 3) break;
            }
            result.put("advice", "根据您的描述，建议先到以下科室就诊。如果症状持续，请及时就医。");
        } else {
            result.put("advice", "根据您描述的症状，建议优先考虑以下科室进行挂号就诊。以上建议仅供参考，具体以医生诊断为准。");
        }

        result.put("depts", depts);
        return result;
    }
}
