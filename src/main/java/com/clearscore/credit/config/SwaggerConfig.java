package com.clearscore.credit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${projectfile.name:clearScoreProject}")
    private String applicationName;
    @Value("${projectfile.description:Clear Score task}")
    private String applicationDescription;
    @Value("${projectfile.version:1.0.0}")
    private String appVersion;

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title(applicationName)
                        .description(applicationDescription)
                        .version(appVersion)
                        .termsOfService("https://www.clearscore.com/za/terms")
                        .license(new License()
                                .name("Privacy Policy")
                                .url("https://www.clearscore.com/za/privacy-policy")
                        )
                );
    }

}
