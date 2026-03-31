package com.uni;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 轻瘦健康应用 - 启动类
 */
@SpringBootApplication
@MapperScan("com.uni.mapper")
@EnableCaching
@EnableTransactionManagement
public class UniApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniApplication.class, args);
        System.out.println("======================================");
        System.out.println("  轻瘦健康应用启动成功！");
        System.out.println("  API文档: http://localhost:8080/doc.html");
        System.out.println("======================================");
    }
}
