package com.syd.ai.controller;


import com.syd.ai.vector.DataLoaderService;
import com.syd.ai.vector.GenerationService;
import com.syd.ai.vector.RetrievalService;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RagController {
    @Resource
    private DataLoaderService dataLoader;
    @Resource
    private RetrievalService retrievalService;
    @Resource
    private GenerationService generationService;

    /**
     * 导入文档数据接口
     */
    @GetMapping("/api/loadDocuments")
    public void loadDocuments() {
        dataLoader.loadDocument();
    }

    /**
     *  获取 rag 聊天结果接口
     * @param question  用户问题
     * @return String  聊天结果
     */
    @GetMapping("/api/rag")
    public String ragChat(@RequestParam String question) {
        List<Document> context = retrievalService.retrieveContext(question);
        return generationService.generateAnswer(question, context);
    }
}