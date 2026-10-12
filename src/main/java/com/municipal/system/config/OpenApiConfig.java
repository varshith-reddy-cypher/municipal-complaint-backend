package com.municipal.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI municipalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Municipal Complaint Prioritization System API")
                        .description("Backend REST API engine for automated severity assessment, dynamic multi-factor priority queue, officer routing, and real-time civic issue workflow monitoring.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Municipal Engineering Team")
                                .email("civic-support@municipal.gov"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
