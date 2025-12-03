package com.alutarb.analytics.shared.infrastructure;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.qdrant.client.QdrantClient;

@Configuration
public class AnalyticsVectorStoreConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean("analyticspublication")
    public VectorStore publicationVectorStore(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
        return buildVectorStore(qdrantClient, embeddingModel, "analyticspublication");
    }

    @Bean("analyticspublication-v2")
    public VectorStore publicationVectorStoreV2(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
        return buildVectorStore(qdrantClient, embeddingModel, "analyticspublication-v2");
    }

    private VectorStore buildVectorStore(QdrantClient client, EmbeddingModel embeddingModel, String name) {
        return QdrantVectorStore.builder(client, embeddingModel)
            .collectionName(name)
            .initializeSchema(true)
            .build();
    }

}
