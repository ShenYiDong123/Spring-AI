package com.syd.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@CrossOrigin
public class OpenAiController {

    @Autowired
    ChatClient botChatClient;


    @CrossOrigin
    @GetMapping(value = "/ai/generateStreamAsString",     produces = "text/event-stream;charset=UTF-8")  // 明确指定UTF-8编码)
    public Flux<String> generateStreamAsString(@RequestParam(value = "message", defaultValue = "讲个笑话") String message) {
        // 创建一个用于接收多条消息的 Sink
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<String> content = botChatClient.prompt().user(message).stream().content();
        content.doOnNext(sink::tryEmitNext) // 推送每条AI流内容
                .doOnComplete(() -> sink.tryEmitComplete())
                .subscribe();
        System.out.printf(message);
        return sink.asFlux();
    }
}
