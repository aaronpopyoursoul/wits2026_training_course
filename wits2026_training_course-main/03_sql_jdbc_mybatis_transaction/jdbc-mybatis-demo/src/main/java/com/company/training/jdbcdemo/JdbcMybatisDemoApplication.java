package com.company.training.jdbcdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.company.training.jdbcdemo.mapper")
public class JdbcMybatisDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JdbcMybatisDemoApplication.class, args);
    }
}