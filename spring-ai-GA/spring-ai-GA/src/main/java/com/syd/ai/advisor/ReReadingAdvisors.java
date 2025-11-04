package com.syd.ai.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;
import java.util.Objects;

/**
 *  重读策略的核心在于让LLMs重新审视输入问题，这借鉴了人类解决问题的思维方式。
 *  通过这种方式，LLMs能够更深入地理解问题，发现复杂的模式，从而在各种推理任务中表现得更加强大。
 * 可以基于BaseAdvisor来实现自定义Advisor， 他实现了重复的代码 提供 模板方法让我们可以专注自己业务编写即可。
 */
public class ReReadingAdvisors implements BaseAdvisor {

    // 默认重读提示模板
    private static final String DEFAULT_USER_TEXT_ADVISE = """
请重新仔细阅读并理解以下问题：
{re2_input_query}

请确保完全理解问题后再给出回答。
""";

    private final PromptTemplate promptTemplate;

    /**
     * 使用默认模板的构造函数
     */
    public ReReadingAdvisors() {
        this(DEFAULT_USER_TEXT_ADVISE);
    }

    /**
     * 使用自定义模板的构造函数
     * @param template 自定义的重读模板
     */
    public ReReadingAdvisors(String template) {
        this.promptTemplate = PromptTemplate.builder()
                .template(Objects.requireNonNull(template, "模板不能为空"))
                .build();
    }


    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        // 空值检查
        Objects.requireNonNull(chatClientRequest, "ChatClientRequest不能为空");
        Objects.requireNonNull(chatClientRequest.prompt(), "Prompt不能为空");
        
        // 获取用户输入的文本
        String userText = chatClientRequest.prompt().getUserMessage().getText();
        if (userText == null || userText.trim().isEmpty()) {
            return chatClientRequest; // 如果用户输入为空，直接返回原请求
        }

        // 使用预构建的模板增强用户输入的文本
        String newUserText = promptTemplate.render(Map.of("re2_input_query", userText));

        // 构建新的请求，保留原始请求的其他属性
        return ChatClientRequest.builder()
                .prompt(Prompt.builder().content(newUserText).build())
                .build();
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
