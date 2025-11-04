package com.github.liyibo1110.demo;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;

/**
 * 向量化例子
 * @author liyibo
 * @date 2025-11-03 17:28
 */
public class TestVector {

    @Test
    public void testVector() {
        QwenEmbeddingModel model = QwenEmbeddingModel.builder()
                .apiKey("myApiKey")
                .build();
        Response<Embedding> embed = model.embed("你好，我叫李博士");
        System.out.println(embed.content());
        System.out.println(embed.content().vector().length);
    }

    @Test
    public void testVectorStore() {
        /** embedding阶段 */
        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
        QwenEmbeddingModel model = QwenEmbeddingModel.builder()
                .apiKey("myApiKey")
                .build();
        TextSegment ts1 = TextSegment.from("""
                预订航班：
                - 通过我们的网站或移动应用程序预订。
                - 预订时需要全额付款。
                - 确保个人信息（姓名、ID等）的准确性，因为更正可能会产生25的费用。
                """);
        Embedding e1 = model.embed(ts1).content();
        store.add(e1, ts1);

        TextSegment ts2 = TextSegment.from("""
                取消预订：
                - 最晚在航班起飞前48小时取消。
                - 取消费用：经济舱75美元，豪华经济舱50美元，商务舱25美元。
                - 退款将在7个工作日内处理。
                """);
        Embedding e2 = model.embed(ts2).content();
        store.add(e2, ts2);

        /** 查询阶段 */
        Embedding query = model.embed("退票要多少钱？").content();
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(query)
                .maxResults(1)
                // .minScore(0.8D)
                .build();
        EmbeddingSearchResult<TextSegment> result = store.search(request);
        result.matches().forEach(match -> {
            System.out.println(match.score());  // 范围为0.0 ~ 1.0
            System.out.println(match.embedded().text());
        });
    }
}
