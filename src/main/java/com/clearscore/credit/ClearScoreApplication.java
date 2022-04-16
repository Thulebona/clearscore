package com.clearscore.credit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.clearscore", exclude = {ErrorMvcAutoConfiguration.class})
public class ClearScoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClearScoreApplication.class, args);
    }

}
