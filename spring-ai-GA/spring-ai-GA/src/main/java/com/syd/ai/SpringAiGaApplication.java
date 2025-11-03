package com.syd.ai;

import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(basePackages = "com.syd")
public class SpringAiGaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiGaApplication.class, args);
    }

}
