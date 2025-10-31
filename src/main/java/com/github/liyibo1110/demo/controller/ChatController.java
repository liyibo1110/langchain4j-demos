package com.github.liyibo1110.demo.controller;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liyibo
 * @date 2025-10-31 17:32
 */
@RestController
@RequestMapping("/ai")
public class ChatController {
    /** 通义千问的模型 */
    @Autowired
    private QwenChatModel chatModel;

    @GetMapping("/chat")
    public String test(@RequestParam(defaultValue="你是谁？") String message) {
        String answer = chatModel.chat(message);
        return answer;
    }
}
