package com.github.liyibo1110.demo;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.document.splitter.DocumentByRegexSplitter;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author liyibo
 * @date 2025-11-03 18:00
 */
public class TestELP {

    @Test
    public void testLoadFile() {
        Document doc = ClassPathDocumentLoader.loadDocument("rag/terms-of-service.txt", new TextDocumentParser());
        // System.out.println(doc.text());
        DocumentByRegexSplitter regexSplitter = new DocumentByRegexSplitter(
                "\\n\\d+\\.",   // 匹配"1. 标题"这样的格式,
                "\n",                 // 保留换行符作为段落连接符
                100, // 每段最长字数
                20, // 自然语言最大重叠字数
                new DocumentByCharacterSplitter(100, 20)
        );
        DocumentBySentenceSplitter sentenceSplitter = new DocumentBySentenceSplitter(
                90, // 每段最长字数
                10 // 自然语言最大重叠字数
        );
        List<TextSegment> segments = regexSplitter.split(doc);
        // System.out.println(segments);

        /** 生成向量数据 */
        QwenEmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                .apiKey("myApiKey")
                .build();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        // System.out.println(embeddings);

        /** 持久化 */
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments); // 额外存原始segment

        /** 查询 */
        Embedding query = embeddingModel.embed("退费费用").content();
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(query)
                // .maxResults(1)
                .build();
        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);
        result.matches().forEach(match -> {
            // System.out.println(match.embedded().text() + "，分数为：" + match.score());
        });

        // 以上操作只是使用了大模型的向量生成和查询测试（注意是QwenEmbeddingModel），并非最终的用户查询

        /** 真正的检索增强阶段 */
        QwenChatModel chatModel = QwenChatModel.builder()
                .apiKey("myApiKey")
                .modelName("qwen3-max")
                .build();
        EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .build();
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .contentRetriever(retriever)
                .build();
        System.out.println(assistant.chat("退费费用"));
    }

    public interface Assistant {
        String chat(String message);
    }
}
