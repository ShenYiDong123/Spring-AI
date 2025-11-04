package com.syd.ai.vector;

import jakarta.annotation.Resource;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;


import java.util.List;


/**
 * ETL类
 */
@Service
public class DataLoaderService {

    @Value("classpath:text-source.txt")
    private org.springframework.core.io.Resource txtResource;

    @Resource
    private VectorStore vectorStore;

    /**
     * 加载业务数据到向量数据库中
     */
    public void loadDocument() {
        // 1、提取: 使用文本读取器
        TextReader textReader = new TextReader(txtResource);
        List<Document> documents = textReader.get();

        // 2、转换：按Token拆分
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(documents);

        // 3、加载: 存储到ES向量数据库
        vectorStore.add(chunks);
    }

}
