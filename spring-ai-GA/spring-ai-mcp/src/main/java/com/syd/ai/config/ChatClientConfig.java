package com.syd.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.model.deepseek.autoconfigure.DeepSeekChatProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Autowired
    AiTools aiTools;

    // 票务助手
    @Bean
    public ChatClient planningChatClient(DeepSeekChatModel chatModel,  // 替换为DeepSeek模型
                                         DeepSeekChatProperties options,  // 替换为DeepSeek配置
                                         ChatMemory chatMemory) {
        // 替换为DeepSeek的选项类
        DeepSeekChatOptions deepSeekChatOptions = DeepSeekChatOptions.fromOptions(options.getOptions());
        deepSeekChatOptions.setTemperature(0.7);  // 温度参数保持不变

        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        # 票务助手任务拆分规则
                        ## 1.要求
                        ### 1.1 根据用户内容识别任务
                        
                        ## 2. 任务
                        ### 2.1 JobType:退票(CANCEL) 要求用户提供姓名和预定号， 或者从对话中提取；
                        ### 2.2 JobType:查票(QUERY) 要求用户提供预定号， 或者从对话中提取；
                        ### 2.3 JobType:其他(OTHER)
                        
                         “严禁随意补全或猜测工具调用参数。
                         参数如缺失或语义不准，请不要补充或随意传递，请直接放弃本次工具调用。”
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(aiTools) // 底层会告诉大模型提供了什么工具
                .defaultOptions(deepSeekChatOptions)  // 使用DeepSeek选项
                .build();
    }

    // 智能客服
    @Bean
    public ChatClient botChatClient(DeepSeekChatModel chatModel,  // 替换为DeepSeek模型
                                    DeepSeekChatProperties options,  // 替换为DeepSeek配置
                                    ChatMemory chatMemory) {

        // 替换为DeepSeek的选项类
        DeepSeekChatOptions deepSeekChatOptions = DeepSeekChatOptions.fromOptions(options.getOptions());
        deepSeekChatOptions.setTemperature(1.2);  // 温度参数保持不变
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        你是XS航空智能客服代理， 请以友好的语气服务用户。
                         “严禁随意补全或猜测工具调用参数。
                         参数如缺失或语义不准，请不要补充或随意传递，请直接放弃本次工具调用。”
                         """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultTools(aiTools) // 底层会告诉大模型提供了什么工具
                .defaultOptions(deepSeekChatOptions)  // 使用DeepSeek选项
                .build();
    }

}
