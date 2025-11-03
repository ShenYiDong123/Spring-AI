package com.syd.ai.config;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class AiTools {

    @Tool(name = "nameAndNumberTools",description = "根据名字和预定号退票")
    public String nameAndNumberTools(
            @ToolParam(description = "名字，真实人名（必填，必须为人的真实姓名，严禁用其他信息代替；如缺失请传null") String name,
            @ToolParam(description = "预定号，不能包含英文，否则提示用户") String number){
        // 具体业务可以先查询是否存在    ，进行兜底
        System.out.println("退票业务执行成功");
        return "成功";
    }
}
