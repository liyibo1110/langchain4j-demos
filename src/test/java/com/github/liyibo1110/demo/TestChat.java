package com.github.liyibo1110.demo;

import dev.langchain4j.model.openai.OpenAiChatModel;
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
    public void test1() {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
        String answer = model.chat("你好，你是谁？");
        System.out.println(answer);
    }
}
