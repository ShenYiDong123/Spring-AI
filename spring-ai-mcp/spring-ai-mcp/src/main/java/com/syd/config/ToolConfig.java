package com.syd.config;

import com.syd.tools.UserToolService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfig {

    /**
     * 暴露接口
     * @return
     */
    @Bean
    public ToolCallbackProvider providerTools(UserToolService userToolService){
        return MethodToolCallbackProvider.builder()
                .toolObjects(userToolService)
                .build();
    }
}
