package com.github.liyibo1110.demo;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.WanxImageModel;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Test;

/**
 * @author liyibo
 * @date 2025-10-28 15:42
 */
public class TestChat {

    /**
     * 这个演示key在新版本已经不能再用了
     */
    @Test
    public void testGPTDemo() {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
        String answer = model.chat("你好，你是谁？");
        System.out.println(answer);
    }

    @Test
    public void testDeepSeek() {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com")
                .apiKey("myApiKey")  // 换成自己的apiKey
                .modelName("deepseek-chat")
                .build();
        String answer = model.chat("你好，你是谁？");
        System.out.println(answer);
    }

    @Test
    public void testDashScope() {
        QwenChatModel model = QwenChatModel.builder()
                //.baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1") // 不需要baseUrl
                .apiKey("myApiKey")  // 换成自己的apiKey
                .modelName("qwen3-max")
                .build();
        String answer = model.chat("你好，你是谁？");
        System.out.println(answer);
    }

    @Test
    public void testOllama() {
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://127.0.0.1:11434")
                .modelName("deepseek-r1:1.5b")  // 这个模型是存在本地的
                .build();
        String answer = model.chat("你好，你是谁？");
        System.out.println(answer);
    }

    @Test
    public void testTextToImage() {
        WanxImageModel model = WanxImageModel.builder()
                .modelName("wan2.2-t2i-plus")
                .apiKey("myApiKey")  // 换成自己的apiKey
                .build();
        Response<Image> response = model.generate("美女");
        System.out.println(response.content().url());
    }

    /**
     * 记忆多轮对话的简单方式（不实用）
     */
    @Test
    public void testDashScopeWithMemory() {
        QwenChatModel model = QwenChatModel.builder()
                .apiKey("myApiKey")  // 换成自己的apiKey
                .modelName("qwen3-max")
                .build();

        // 构建第一次的内容
        UserMessage um1 = UserMessage.userMessage("你好，我是李博士");
        ChatResponse cr1 = model.chat(um1);
        AiMessage am1 = cr1.aiMessage();
        System.out.println(am1.text());
        System.out.println("------");

        // 开始构建第二次的内容，要带上前面的问和答
        UserMessage um2 = UserMessage.userMessage("我是谁来着？");
        ChatResponse cr2 = model.chat(um1, am1, um2);
        AiMessage am2 = cr2.aiMessage();
        System.out.println(am2.text());
    }
}
