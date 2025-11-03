package com.syd.ai;

import com.syd.ai.advisor.ReReadingAdvisors;
import com.syd.ai.entity.Address;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootTest(classes = SpringAiGaApplication.class)
class SpringAiGaApplicationTests {

    //<artifactId>spring-ai-starter-model-deepseek</artifactId> 会为你增加自动配置类
    // 其中DeepSeekChatModel这个就是专门负责智能对话的。
    @Autowired
    DeepSeekChatModel chatModel;

    @Autowired
    ChatClient.Builder chatClientBuilder;

    @Autowired
    ChatMemory chatMemory;

    @Test
    void contextLoads() {
    }


    /**
     * 同步阻塞对话
     */
    @Test
    public void deepSeekChatModelCall() {
        String call = chatModel.call("你是谁");
        System.out.println(call);
    }

    /**
     * 流式对话
     */
    @Test
    public void deepSeekChatModelStreamCall() {
        Flux<String> stream = chatModel.stream("你是谁");
        // 阻塞输出
        stream.toIterable().forEach(s -> System.out.printf(s));
    }

    /**
     * options配置选项
     */
    @Test
    public void chatOptionsTest() {
        DeepSeekChatOptions deepSeekChatOptions = DeepSeekChatOptions.builder().build();
        /**
         * temperature（温度）
         * 0-2  浮点数值
         * 数值越高  更有创造性   热情
         * 数值越低  保守
         * ● 实战用法一般建议选 0.5~0.8 作为日常生产起点，需要根据业务不断测试调整。
         */

        // 也可以通过配置文件统一配置
        deepSeekChatOptions.setTemperature(1.9d);
        //● 需要简洁回复、打分、列表、短摘要等，建议小值（如10~50）。
        //● 防止用户跑长对话导致无关内容或花费过多token费用。
        //● 如果遇到生成内容经常被截断，可以适当配置更大maxTokens。
        deepSeekChatOptions.setMaxTokens(10);

        Prompt prompt = new Prompt("请描述一下李白的一首短诗",deepSeekChatOptions);
        ChatResponse chatResponse = chatModel.call(prompt);
        System.out.println(chatResponse.getResult().getOutput().getText());
    }


    /**
     * 流式对话(深度思考)
     */
    @Test
    public void deepSeekChatStreamReasoning() {
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model("deepseek-reasoner").build();

        Prompt prompt = new Prompt("请写一句诗描述清晨", options);
        Flux<ChatResponse> stream = chatModel.stream(prompt);
        System.out.println("深度思考内容：");
        stream.toIterable().forEach(res -> {
            DeepSeekAssistantMessage assistantMessage =  (DeepSeekAssistantMessage)res.getResult().getOutput();
            String reasoningContent = assistantMessage.getReasoningContent();
            System.out.print(reasoningContent);
        });
        System.out.println("以下是答案");
        System.out.println("--------------------------------------------");
        stream.toIterable().forEach(res -> {
            DeepSeekAssistantMessage assistantMessage =  (DeepSeekAssistantMessage)res.getResult().getOutput();
            String content = assistantMessage.getText();
            if(StringUtils.isNotBlank(content)) {
                System.out.print(content);
            }
        });
    }

    /**
     * promot提示词
     * 在生成式人工智能中，创建提示对于开发人员来说是一项至关重要的任务。
     * 这些提示的质量和结构会显著影响人工智能输出的有效性。
     * 投入时间和精力设计周到的提示可以显著提升人工智能的成果。
     * 例如，一项重要的研究表明，以“深呼吸，一步一步解决这个问题”作为提示开头，
     * 可以显著提高解决问题的效率。这凸显了精心选择的语言对生成式人工智能系统性能的影响。
     */
    @Value("classpath:/files/systemPrompt.st")
    org.springframework.core.io.Resource systemResource;

    @Test
    public void deepSeekChatPrompt() {
        ChatClient chatClient = chatClientBuilder.defaultSystem(systemResource).build();
        String content = chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.param("name","沈yi").param("age","18").param("sex","男"))
                .user("你好").call().content();
        System.out.println(content);

    }


    ChatClient chatClient;
    /**
     * 对话拦截
     */
    @Test
    public void testChatAdvisorTest() {
        String content =
                chatClient.prompt().user("你好").call().content();
        System.out.println(content);
    }



    /**
     * 对话记忆
     */
    @Test
    public void testChatMemory() {
        String content =
                chatClient.prompt().user("我今年18岁").call().content();
        System.out.println(content);
        System.out.println("--------------------------------------------------------------------------");
        content =
                chatClient.prompt().user("我今年多少岁").call().content();
        System.out.println(content);
    }



    /**
     * 简单的拦截器及自定义拦截器，可以获取请求参数和返回参数（配置文件可配置日志级别）
     * dvisor只有结合ChatClient才能用！   是SpringAi上层提供的。  模型底层并没有这个东西
     */
    @BeforeEach
    public void init() {
        chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(//new SimpleLoggerAdvisor(), // 简单日志拦截
                        //new ReReadingAdvisors(), // 重读拦截器
                        PromptChatMemoryAdvisor.builder(chatMemory).build() // 配置官方的对话记忆
                ).build();
    }

    /**
     * 对话记忆(设置最大上限)
     * 设置对话ID
     */
    @Test
    public void testMaxChatMemory() {
        String content =
                chatClient.prompt()
                        .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"1"))
                        .user("我叫沈yi").call().content();
        System.out.println(content);
        System.out.println("--------------------------------------------------------------------------");
        content =
                chatClient.prompt()
                        .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"1"))
                        .user("我叫什么").call().content();
        System.out.println(content);
        System.out.println("--------------------------------------------------------------------------");
        content =
                chatClient.prompt()
                        .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"2"))
                        .user("我叫什么").call().content();
        System.out.println(content);

    }


    /**
     * 以Boolean为例 ， 在agent中可以用于判定用于的内容2个分支，  不同的分支走不同的逻辑
     */
    @Test
    public void testBoolOut() {
        Boolean isComplain = chatClient.prompt().system("""
                                    请判断用户信息是否表达了投诉意图?
                                    只能用 true 或 false 回答，不要输出多余内容
                                    """)
                                    .user("我要退货，不然投诉")
                .call().entity(Boolean.class);

        if(isComplain) {
            System.out.println("请求人工客服");
        }else {
            System.out.println("继续使用ai回复");
        }
    }


    /**
     * Pojo类型：
     * 用购物APP应该见过复制一个地址， 自动为你填入每个输入框。  用大模型轻松完成！
     */
    @Test
    public void testPojoOut() {
        Address address = chatClient.prompt().system("""
                                    请从下面这条文本中提取收货信息
                        """)
                .user("收货人：张三，电话13588888888，地址：广东省汕头市金平区文一西路100号8幢202室")
                .call()
                .entity(Address.class);
        System.out.println(address);
    }



}


