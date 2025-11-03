package com.syd.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Component
public class UserToolService {

    public UserToolService() {
        System.out.println("UserToolService 初始化成功"); // 启动时若打印，说明被 Spring 扫描
    }

    Map<String,Double> userScore = Map.of(
            "xushu",99.0,
            "zhangsan",2.0,
            "lisi",3.0);


    /**
     * http://127.0.0.1:8080/ai/generateStreamAsString?message=查询徐庶分数
     * @param userName
     * @return
     */
    @Tool(description = "获取用户分数")
    public String getUserSource(@ToolParam(description = "获取用户姓名")String userName) {
        System.out.println("调用开始");
        if(userScore.containsKey(userName)){
            System.out.println("username:"+userName);
            return userScore.get(userName).toString();
        }

        System.out.println("未检索到当前用户:"+userName);
        return "未检索到当前用户"+userName;
    }
}
