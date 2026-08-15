package com.dorm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 报修管理全链路集成测试（独立文件）
 *
 * <p>前置条件：本机 MySQL 已启动并执行 scripts/create.sql、scripts/init-data.sql，
 * 数据库密码为 root（application-local.yml 已配置）。
 *
 * <p>使用 @Transactional 事务回滚隔离，测试产生的数据在用例结束后回滚。
 * 覆盖：学生提交（含无入住 4001）、我的报修、管理员分页、状态流转（0→1→2）、
 * 非法流转 4002/4003、完成回填处理人与时间、超时标识 isOverdue、操作日志、角色越权。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RepairFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** 有入住记录的种子学生 */
    private static final String STUDENT_WITH_CHECKIN = "20240001";

    /** 测试中由管理员创建、无入住记录的学生学号 */
    private static final String STUDENT_WITHOUT_CHECKIN = "20991001";

    private static final String PASSWORD = "123456";

    private String adminToken;
    private String studentToken;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = login("admin", PASSWORD);
        studentToken = login(STUDENT_WITH_CHECKIN, PASSWORD);
    }

    // ==================== 提交报修 ====================

    @Test
    @DisplayName("有入住记录的学生提交报修成功")
    void student_submitRepair_success() throws Exception {
        mockMvc.perform(post("/repair")
                        .header("satoken", studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("repairType", 0, "content", "水龙头漏水", "contactPhone", "13800000001"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    @DisplayName("无有效入住记录的学生提交报修返回 4001")
    void student_withoutCheckin_submitRepair_fail() throws Exception {
        // 管理员创建一个无入住记录的学生账号
        createStudent(adminToken, STUDENT_WITHOUT_CHECKIN);
        String token = login(STUDENT_WITHOUT_CHECKIN, PASSWORD);
        mockMvc.perform(post("/repair")
                        .header("satoken", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("repairType", 1, "content", "床板损坏", "contactPhone", "13800000002"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4001));
    }

    // ==================== 查询 ====================

    @Test
    @DisplayName("学生分页查询本人报修，含联查字段与超时标识")
    void student_queryMyRepairs() throws Exception {
        long repairId = submitRepair();
        mockMvc.perform(get("/repair/my")
                        .header("satoken", studentToken)
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].repairId").value(repairId))
                .andExpect(jsonPath("$.data.list[0].username").value(STUDENT_WITH_CHECKIN))
                .andExpect(jsonPath("$.data.list[0].realName").isNotEmpty())
                .andExpect(jsonPath("$.data.list[0].roomNo").isNotEmpty())
                .andExpect(jsonPath("$.data.list[0].isOverdue").value(false));
    }

    @Test
    @DisplayName("管理员分页查询全部报修，支持状态筛选")
    void admin_queryRepairPage() throws Exception {
        long repairId = submitRepair();
        mockMvc.perform(get("/repair/page")
                        .header("satoken", adminToken)
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].repairId").value(repairId))
                .andExpect(jsonPath("$.data.list[0].buildingName").isNotEmpty())
                .andExpect(jsonPath("$.data.list[0].handlerName").doesNotExist());
    }

    // ==================== 状态流转 ====================

    @Test
    @DisplayName("正常流转：待处理→处理中→已完成，回填处理人与时间")
    void handleRepair_normalFlow() throws Exception {
        long repairId = submitRepair();

        // 待处理 → 处理中
        mockMvc.perform(put("/repair/" + repairId + "/handle")
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("status", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 处理中 → 已完成（必须填处理结果）
        mockMvc.perform(put("/repair/" + repairId + "/handle")
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("status", 2, "handleResult", "已安排维修"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 管理端查询断言：状态=2、处理人、处理时间已回填
        mockMvc.perform(get("/repair/page")
                        .header("satoken", adminToken)
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("status", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].repairId").value(repairId))
                .andExpect(jsonPath("$.data.list[0].status").value(2))
                .andExpect(jsonPath("$.data.list[0].handlerName").value("系统管理员"))
                .andExpect(jsonPath("$.data.list[0].handleTime").isNotEmpty())
                .andExpect(jsonPath("$.data.list[0].handleResult").value("已安排维修"));
    }

    @Test
    @DisplayName("非法流转校验：跳级/逆向/终态再流转/缺少处理结果均被拒绝")
    void handleRepair_illegalFlow() throws Exception {
        long repairId = submitRepair();

        // 待处理 → 直接已完成：4002 跳级
        mockMvc.perform(put("/repair/" + repairId + "/handle")
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("status", 2, "handleResult", "越级"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4002));

        // 待处理 → 处理中：合法
        mockMvc.perform(put("/repair/" + repairId + "/handle")
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("status", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 处理中 → 处理中：4002 非法目标
        mockMvc.perform(put("/repair/" + repairId + "/handle")
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("status", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4002));

        // 处理中 → 已完成但缺处理结果：400
        mockMvc.perform(put("/repair/" + repairId + "/handle")
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("status", 2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        // 处理中 → 已完成：合法
        mockMvc.perform(put("/repair/" + repairId + "/handle")
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("status", 2, "handleResult", "维修完成"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 已完成 → 再次流转：4003 终态
        mockMvc.perform(put("/repair/" + repairId + "/handle")
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("status", 2, "handleResult", "再次处理"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4003));

        // 不存在的报修单：4003
        mockMvc.perform(put("/repair/999999/handle")
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("status", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4003));
    }

    // ==================== 超时标识 ====================

    @Test
    @DisplayName("isOverdue：待处理提交超24小时为 true")
    void repair_overdueFlag() throws Exception {
        long repairId = submitRepair();
        // 将该报修提交时间回拨 25 小时，仅查询一次（避免 MyBatis 一级缓存命中）
        jdbcTemplate.update("UPDATE dorm_repair SET create_time = ? WHERE repair_id = ?",
                LocalDateTime.now().minusHours(25), repairId);
        mockMvc.perform(get("/repair/my")
                        .header("satoken", studentToken)
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].repairId").value(repairId))
                .andExpect(jsonPath("$.data.list[0].isOverdue").value(true));
    }

    // ==================== 操作日志 ====================

    @Test
    @DisplayName("管理员处理报修被 AOP 记录操作日志")
    void operationLog_recorded_for_handle() throws Exception {
        long repairId = submitRepair();
        mockMvc.perform(put("/repair/" + repairId + "/handle")
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("status", 1))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/log/page")
                        .header("satoken", adminToken)
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("module", "报修管理")
                        .param("operationType", "修改"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].module").value("报修管理"))
                .andExpect(jsonPath("$.data.list[0].operationType").value("修改"))
                .andExpect(jsonPath("$.data.list[0].operatorName").value("系统管理员"));
    }

    // ==================== 角色越权 ====================

    @Test
    @DisplayName("角色越权：学生访问管理员接口、管理员访问学生接口均返回 403")
    void role_access_control() throws Exception {
        // 学生访问管理员分页：403
        mockMvc.perform(get("/repair/page")
                        .header("satoken", studentToken)
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        // 管理员访问学生我的报修：403
        mockMvc.perform(get("/repair/my")
                        .header("satoken", adminToken)
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    // ==================== 私有辅助方法 ====================

    /** 登录并返回 Token */
    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("username", username, "password", password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        String token = root.path("data").path("token").asText();
        assertFalse(token.isEmpty(), "登录 Token 不应为空");
        return token;
    }

    /** 管理员创建学生账号 */
    private void createStudent(String token, String username) throws Exception {
        mockMvc.perform(post("/user")
                        .header("satoken", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("username", username, "realName", "无入住学生",
                                "gender", "男", "phone", "13900000003"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /** 学生提交报修并返回 repairId */
    private long submitRepair() throws Exception {
        MvcResult result = mockMvc.perform(post("/repair")
                        .header("satoken", studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("repairType", 0, "content", "宿舍灯管不亮", "contactPhone", "13800000001"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        long repairId = root.path("data").asLong();
        assertTrue(repairId > 0, "返回的 repairId 应大于 0");
        return repairId;
    }

    /** 对象转 JSON 字符串 */
    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    /** 键值对组装 Map（兼容 JDK 8） */
    private Map<String, Object> map(Object... keyValues) {
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            result.put((String) keyValues[i], keyValues[i + 1]);
        }
        return result;
    }
}
