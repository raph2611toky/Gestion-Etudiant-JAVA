package com.studentmanagement.student_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;;

@SpringBootApplication(scanBasePackages = "com.studentmanagement")
@EnableJpaRepositories(basePackages = "com.studentmanagement.repository")
@EntityScan(basePackages = "com.studentmanagement.model")
public class StudentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentApiApplication.class, args);
    }

    @Configuration
    public static class WebConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:D:\\TOKY\\PROJET\\JAVA\\gestion des etudiants\\student-api\\uploads\\");
        }
    }
}