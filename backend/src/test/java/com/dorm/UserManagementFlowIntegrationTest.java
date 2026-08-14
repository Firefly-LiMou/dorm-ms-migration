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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 登录 + 学生账号管理全链路集成测试（独立文件）
 *
 * <p>前置条件：本机 MySQL 已启动并执行 scripts/create.sql、scripts/init-data.sql，
 * 数据库密码为 root（backend/src/main/resources/application-local.yml 已配置）。
 *
 * <p>使用 @Transactional 事务回滚隔离，测试产生的数据（学生账号、操作日志）在用例结束后回滚，
 * 不污染数据库。覆盖：管理员登录、错误密码登录、创建/学号唯一校验、分页筛选、编辑与禁用/启用、
 * 重置密码后学生登录、操作日志自动记录、学生越权访问。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserManagementFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /** 管理员账号（种子数据） */
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "123456";

    /** 测试用学生学号（每次用例唯一，事务回滚可安全重跑） */
    private static final String TEST_STUDENT_NO_1 = "20990001";
    private static final String TEST_STUDENT_NO_2 = "20990002";
    private static final String TEST_STUDENT_NO_3 = "20990003";

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    // ==================== 认证 ====================

    @Test
    @DisplayName("管理员登录成功，返回 Token 与用户信息")
    void login_admin_success() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("username", ADMIN_USERNAME, "password", ADMIN_PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.userInfo.username").value(ADMIN_USERNAME))
                .andExpect(jsonPath("$.data.userInfo.role").value("admin"));
    }

    @Test
    @DisplayName("密码错误登录失败，返回业务错误码 1001")
    void login_wrongPassword_fail() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("username", ADMIN_USERNAME, "password", "wrongpass"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1001));
    }

    // ==================== 创建与学号唯一校验 ====================

    @Test
    @DisplayName("创建学生账号成功，重复学号返回 1003")
    void createStudent_success_then_duplicate_fail() throws Exception {
        long userId = createStudent(TEST_STUDENT_NO_1);

        // 学号唯一校验：重复创建返回 1003
        mockMvc.perform(post("/user")
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("username", TEST_STUDENT_NO_1, "realName", "重复学生"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1003));
    }

    // ==================== 分页查询 ====================

    @Test
    @DisplayName("分页查询支持按学号、姓名、状态筛选")
    void pageQuery_filterByUsernameAndStatus() throws Exception {
        long userId = createStudent(TEST_STUDENT_NO_2);
        // 编辑为禁用，便于状态筛选断言
        mockMvc.perform(put("/user/" + userId)
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("status", 0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 按学号模糊筛选
        mockMvc.perform(get("/user/page")
                        .header("satoken", adminToken)
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("username", TEST_STUDENT_NO_2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].username").value(TEST_STUDENT_NO_2))
                .andExpect(jsonPath("$.data.list[0].status").value(0));

        // 按状态筛选：禁用账号列表包含新账号
        mockMvc.perform(get("/user/page")
                        .header("satoken", adminToken)
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[*].status").value(everyItem(is(0))));
    }

    // ==================== 编辑 / 禁用 / 启用 ====================

    @Test
    @DisplayName("编辑学生信息与禁用/启用状态流转")
    void updateUser_editInfo_and_toggleStatus() throws Exception {
        long userId = createStudent(TEST_STUDENT_NO_3);

        // 编辑信息 + 禁用
        mockMvc.perform(put("/user/" + userId)
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("realName", "改名学生", "status", 0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/user/page")
                        .header("satoken", adminToken)
                        .param("username", TEST_STUDENT_NO_3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].realName").value("改名学生"))
                .andExpect(jsonPath("$.data.list[0].status").value(0));

        // 启用
        mockMvc.perform(put("/user/" + userId)
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("status", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/user/page")
                        .header("satoken", adminToken)
                        .param("username", TEST_STUDENT_NO_3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].status").value(1));
    }

    // ==================== 重置密码 ====================

    @Test
    @DisplayName("重置密码后学生可用初始密码 123456 登录")
    void resetPassword_then_studentLogin_success() throws Exception {
        long userId = createStudent(TEST_STUDENT_NO_1);

        mockMvc.perform(put("/user/" + userId + "/password/reset")
                        .header("satoken", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 使用初始密码登录成功，验证密码已重置
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("username", TEST_STUDENT_NO_1, "password", ADMIN_PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userInfo.role").value("student"));
    }

    // ==================== 操作日志（AOP 自动记录） ====================

    @Test
    @DisplayName("管理员写操作被 AOP 自动记录，可按模块与类型分页查询")
    void operationLog_recorded_for_create() throws Exception {
        createStudent(TEST_STUDENT_NO_2);

        mockMvc.perform(get("/log/page")
                        .header("satoken", adminToken)
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("module", "用户管理")
                        .param("operationType", "新增"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].module").value("用户管理"))
                .andExpect(jsonPath("$.data.list[0].operationType").value("新增"))
                .andExpect(jsonPath("$.data.list[0].operatorName").value("系统管理员"));
    }

    // ==================== 权限控制 ====================

    @Test
    @DisplayName("学生角色访问管理员接口被拒绝，返回 403")
    void student_cannot_access_adminApi() throws Exception {
        String studentToken = login("20240001", ADMIN_PASSWORD);

        mockMvc.perform(get("/user/page")
                        .header("satoken", studentToken)
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

    /** 管理员创建学生账号并返回 userId */
    private long createStudent(String username) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("realName", "测试学生");
        body.put("gender", "男");
        body.put("phone", "13900000000");

        MvcResult result = mockMvc.perform(post("/user")
                        .header("satoken", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        long userId = root.path("data").asLong();
        assertTrue(userId > 0, "创建返回的 userId 应大于 0");
        return userId;
    }

    /** 对象转 JSON 字符串 */
    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    /** 键值对组装 Map（兼容 JDK 8，替代 Map.of） */
    private Map<String, Object> map(Object... keyValues) {
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            result.put((String) keyValues[i], keyValues[i + 1]);
        }
        return result;
    }
}
