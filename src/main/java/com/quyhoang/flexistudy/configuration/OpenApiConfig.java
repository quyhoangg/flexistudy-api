package com.quyhoang.flexistudy.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        // Xác định server URL động: nếu deploy lên Render, Swagger sẽ tự hiểu đúng
        String serverUrl = System.getenv("RENDER_EXTERNAL_URL") != null
                ? System.getenv("RENDER_EXTERNAL_URL") + contextPath
                : "http://localhost:8080" + contextPath;

        return new OpenAPI()
                .info(new Info()
                        .title("Flexistudy Service API")
                        .version("1.0.0")
                        .description("API documentation for Flexistudy project"))
                .servers(List.of(new Server().url(serverUrl)));
    }
}
