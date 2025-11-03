package com.github.liyibo1110.demo.controller;

import com.github.liyibo1110.demo.config.Config;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * @author liyibo
 * @date 2025-10-31 17:32
 */
@RestController
@RequestMapping("/ai")
public class ChatController {
    /** 通义千问的同步对话模型 */
    @Autowired
    private QwenChatModel chatModel;

    /** 通义千问的流式对话模型 */
    @Autowired
    private QwenStreamingChatModel chatStreamingModel;

    @Autowired
    private Config.Assistant assistant;

    @Autowired
    private Config.AssistantUnique assistantUnique;

    /**
     * 同步对话
     * @param message
     * @return
     */
    @GetMapping("/chat")
    public String chat(@RequestParam(defaultValue="你是谁？") String message) {
        String answer = chatModel.chat(message);
        return answer;
    }

    /**
     * 流式对话，要想输出到前端浏览器，需要使用webflux
     * @param message
     * @return
     */
    @GetMapping("/stream")
    public String stream(@RequestParam(defaultValue="你是谁？") String message) {
        chatStreamingModel.chat(message, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String response) {
                System.out.println(response);
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                System.out.println("[结束]");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        });
        return "OK";
    }

    @GetMapping("/memoryChat")
    public String memoryChat(@RequestParam(defaultValue="我是李博士") String message) {
        /*String answer1 = assistant.chat(message);
        String answer2 = assistant.chat("我是谁来着？");
        return answer1 + "\r\n =========================================\r\n" + answer2;*/
        String answer = assistant.chat(message, LocalDate.now().toString());
        return answer;
    }

    @GetMapping("/memoryChatUnique")
    public String memoryChatUnique(
            Integer userId,
            @RequestParam(defaultValue="我是李博士") String message) {
        String answer1 = assistantUnique.chat(userId, message);
        String answer2 = assistantUnique.chat(userId, "我是谁来着？");
        return answer1 + "\r\n =========================================\r\n" + answer2;
    }
}
