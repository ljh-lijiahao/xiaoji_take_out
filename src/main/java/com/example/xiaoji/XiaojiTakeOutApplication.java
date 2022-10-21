package com.example.xiaoji;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching
public class XiaojiTakeOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiaojiTakeOutApplication.class, args);
    }

}
