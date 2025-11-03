package com.syd.ai.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 *  重读策略的核心在于让LLMs重新审视输入问题，这借鉴了人类解决问题的思维方式。
 *  通过这种方式，LLMs能够更深入地理解问题，发现复杂的模式，从而在各种推理任务中表现得更加强大。
 * 可以基于BaseAdvisor来实现自定义Advisor， 他实现了重复的代码 提供 模板方法让我们可以专注自己业务编写即可。
 */
public class ReReadingAdvisors implements BaseAdvisor {

    // 用户提示词"重读"模板
    private static final String DEFAULT_USER_TEXT_ADVISE = """
      {re2_input_query}
      Read the question again: {re2_input_query}
      """;


    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        // 获取用户输入的文本
        String userText = chatClientRequest.prompt().getUserMessage().getText();

        // 定义 重读 模板
        PromptTemplate promptTemplate = PromptTemplate.builder().template(DEFAULT_USER_TEXT_ADVISE).build();

        // 增强用户输入的文本
        String newUserText = promptTemplate.render(Map.of("re2_input_query", userText));

        // 重新请求
        Prompt prompt = Prompt.builder().content(newUserText).build();
        ChatClientRequest newChatClientRequest = ChatClientRequest.builder().prompt(prompt).build();


        return newChatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        //我们不做任何处理
        return chatClientResponse;
    }

    /**
     * 顺序，优先级
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
