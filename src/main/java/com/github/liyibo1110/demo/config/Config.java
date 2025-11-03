package com.github.liyibo1110.demo.config;

import com.alibaba.dashscope.assistants.Assistant;
import com.github.liyibo1110.demo.service.ToolService;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liyibo
 * @date 2025-10-31 18:22
 */
@Configuration(proxyBeanMethods=false)
public class Config {
    @Bean
    public Assistant assistant(ChatModel qwenChatModel,
                               StreamingChatModel qwenStreamingChatModel,
                               ToolService toolService) {
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);
        // 生成动态代理对象
        Assistant assistant = AiServices.builder(Assistant.class)
                .tools(toolService)
                .chatModel(qwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemory(memory)
                .build();
        return assistant;
    }

    @Bean
    public AssistantUnique assistantUnique(ChatModel qwenChatModel, StreamingChatModel qwenStreamingChatModel) {
        // 生成动态代理对象
        AssistantUnique assistant = AiServices.builder(AssistantUnique.class)
                .chatModel(qwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder().maxMessages(10).id(memoryId).build())
                .build();
        return assistant;
    }

    @Bean
    public AssistantUnique assistantUniqueStore(ChatModel qwenChatModel, StreamingChatModel qwenStreamingChatModel) {
        PersistentChatMemoryStore store = new PersistentChatMemoryStore();
        ChatMemoryProvider provider = memoryId -> MessageWindowChatMemory.builder()
                .maxMessages(10)
                .id(memoryId)
                .chatMemoryStore(store) // 加入自定义的存储实现
                .build();

        // 生成动态代理对象
        AssistantUnique assistant = AiServices.builder(AssistantUnique.class)
                .chatModel(qwenChatModel)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemoryProvider(provider)
                .build();
        return assistant;
    }

    /**
     * 聊天助手（具有记忆功能，但是不能区分不同用户）
     */
    public interface Assistant {
        /** 同步对话（附带特定职责） */
        String chat(String message);
        /** 流式对话 */
        TokenStream stream(String message);
        /** 同步对话（附带特定职责） */
        @SystemMessage("""
                您是中国航空公司的客户聊天支持代理。
                请以友好、乐于助人且愉快的方式来回复。
                您正在通过在线聊天系统与客户互动。
                在提供有关预订或取消预订的信息之前，您必须始终从用户处获取以下信息：预定号、客户姓名。
                请讲中文。
                今天的日期是{{currentDate}}
                """)
        String chat(@UserMessage String message, @V("currentDate") String currentDate);

    }

    /**
     * 聊天助手（具有记忆功能，同时可以区分不同用户）
     */
    public interface AssistantUnique {
        String chat(@MemoryId int memoryId, @UserMessage String message);
        TokenStream stream(@MemoryId int memory, @UserMessage String message);
    }
}
