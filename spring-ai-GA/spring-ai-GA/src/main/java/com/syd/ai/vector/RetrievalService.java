package com.syd.ai.vector;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 检索服务类
 */
@Service
public class RetrievalService {

    @Resource
    private VectorStore vectorStore;

    public List<Document> retrieveContext(String query) {
        // 检索前5个最相关的文档片段
        return vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(5).build()
        );
    }
}