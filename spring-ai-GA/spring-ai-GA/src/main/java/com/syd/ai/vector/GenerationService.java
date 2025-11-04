package com.syd.ai.vector;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import org.springframework.ai.document.Document;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 结合检索结果生成答案
 */
@Service
public class GenerationService {

    private ChatClient botChatClient;


    public String generateAnswer(String question, List<Document> context) {
        // 知识库文本
        String contextText = context.stream()
                .map(Document::getText)
                .collect(Collectors.joining());


        // 提示词模板
        String PROMPT_TEMPLATE = """
                请根据以下上下文回答问题：
                {context}
                        
                用户问题：{question}
                回答时需注意：
                1. 仅基于上下文内容回答
                2. 若上下文不相关，回答“暂未收录相关信息”
                """;
        Prompt prompt = new Prompt(new UserMessage(
                PROMPT_TEMPLATE
                        .replace("{context}", contextText)
                        .replace("{question}", question)
        ));

        // 调用大模型
        return botChatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getText();
    }
}