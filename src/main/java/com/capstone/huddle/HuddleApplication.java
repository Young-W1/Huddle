package com.capstone.huddle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableJpaRepositories(basePackages = "com.capstone.huddle")
@SpringBootApplication
public class HuddleApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuddleApplication.class, args);
    }

}
