package com.github.qq120011676.minio.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 程序入口类
 */
@EnableCaching
@SpringBootApplication(scanBasePackages = {"com.github.qq120011676.ladybird"}, scanBasePackageClasses = Application.class)
public class Application {
    /**
     * 程序入口
     *
     * @param args 入口程序参数
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
