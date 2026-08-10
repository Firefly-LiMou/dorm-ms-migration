package com.dorm.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 4.x 文档配置（基于 SpringDoc/OpenAPI3），访问 /api/doc.html 在线调试
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("高校公寓管理系统 API")
                        .description("学生 / 管理员双角色，覆盖认证、宿舍入住、报修管理三大核心模块")
                        .version("v1.0"));
    }
}
