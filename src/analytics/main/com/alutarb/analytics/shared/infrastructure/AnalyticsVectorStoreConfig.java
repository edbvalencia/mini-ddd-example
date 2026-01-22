package com.alutarb.analytics.shared.infrastructure;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;

@Configuration
public class AnalyticsVectorStoreConfig {

    private static final int EMBEDDING_DIMENSIONS = 768;

    @Bean
    public QdrantClient qdrantClient(
        @Value("${spring.ai.vectorstore.qdrant.host}") String host,
        @Value("${spring.ai.vectorstore.qdrant.port}") int port,
        @Value("${spring.ai.vectorstore.qdrant.use-tls:false}") boolean useTls
    ) {
        var channelBuilder = io.grpc.ManagedChannelBuilder
            .forAddress(host, port)
            .maxInboundMessageSize(50 * 1024 * 1024);

        if (!useTls) {
            channelBuilder.usePlaintext();
        }

        return new QdrantClient(
            io.qdrant.client.QdrantGrpcClient.newBuilder(channelBuilder.build()).build()
        );
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    @Primary
    public EmbeddingModel normalizingEmbeddingModel(@Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        return new NormalizingEmbeddingModel(embeddingModel);
    }

    @Bean("analyticspublication")
    public VectorStore publicationVectorStore(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
        ensureCollection(qdrantClient, "analyticspublication");
        return buildVectorStore(qdrantClient, embeddingModel, "analyticspublication");
    }

    @Bean("analyticspublication-v2")
    public VectorStore publicationVectorStoreV2(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
        ensureCollection(qdrantClient, "analyticspublication-v2");
        return buildVectorStore(qdrantClient, embeddingModel, "analyticspublication-v2");
    }

    @Bean("segmentationpublication")
    public VectorStore segmentationPublicationVectorStore(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
        ensureCollection(qdrantClient, "segmentationpublication");
        return buildVectorStore(qdrantClient, embeddingModel, "segmentationpublication");
    }

    private VectorStore buildVectorStore(QdrantClient client, EmbeddingModel embeddingModel, String name) {
        return QdrantVectorStore.builder(client, embeddingModel)
            .collectionName(name)
            .initializeSchema(false)
            .build();
    }

    private void ensureCollection(QdrantClient client, String collectionName) {
        try {
            boolean exists = client.collectionExistsAsync(collectionName).get();
            if (exists) {
                return;
            }
            client.createCollectionAsync(
                collectionName,
                VectorParams.newBuilder()
                    .setSize(EMBEDDING_DIMENSIONS)
                    .setDistance(Distance.Dot)
                    .build()
            ).get();
        } catch (Exception e) {
            throw new RuntimeException("Error ensuring Qdrant collection: " + collectionName, e);
        }
    }

}
