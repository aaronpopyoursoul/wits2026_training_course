package com.company.training.auditdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class TrainingAuditDemoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TrainingAuditDemoApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TrainingAuditDemoApplication.class);
    }
}
