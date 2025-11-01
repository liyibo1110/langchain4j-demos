package com.github.liyibo1110.demo.config;

import com.alibaba.dashscope.assistants.Assistant;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liyibo
 * @date 2025-10-31 18:22
 */
@Configuration(proxyBeanMethods=false)
public class Config {

    @Bean
    public Assistant assistant(ChatModel qwenChatModel, StreamingChatModel qwenStreamingChatModel) {
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);
        // 生成动态代理对象
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(qwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemory(memory)
                .build();
        return assistant;
    }

    public interface  Assistant {
        /** 同步对话 */
        String chat(String message);
        /** 流式对话 */
        TokenStream stream(String message);
    }
}
