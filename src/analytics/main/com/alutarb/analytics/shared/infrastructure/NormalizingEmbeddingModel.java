package com.alutarb.analytics.shared.infrastructure;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

public class NormalizingEmbeddingModel implements EmbeddingModel {

    private final EmbeddingModel delegate;

    public NormalizingEmbeddingModel(EmbeddingModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        EmbeddingResponse response = delegate.call(request);
        List<Embedding> normalizedEmbeddings = response.getResults().stream()
            .map(this::normalizeEmbedding)
            .toList();
        return new EmbeddingResponse(normalizedEmbeddings, response.getMetadata());
    }

    @Override
    public float[] embed(Document document) {
        float[] embedding = delegate.embed(document);
        return normalizeVector(embedding);
    }

    @Override
    public float[] embed(String text) {
        float[] embedding = delegate.embed(text);
        return normalizeVector(embedding);
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        List<float[]> embeddings = delegate.embed(texts);
        return embeddings.stream()
            .map(this::normalizeVector)
            .toList();
    }

    @Override
    public EmbeddingResponse embedForResponse(List<String> texts) {
        EmbeddingResponse response = delegate.embedForResponse(texts);
        List<Embedding> normalizedEmbeddings = response.getResults().stream()
            .map(this::normalizeEmbedding)
            .toList();
        return new EmbeddingResponse(normalizedEmbeddings, response.getMetadata());
    }

    @Override
    public int dimensions() {
        return delegate.dimensions();
    }

    private Embedding normalizeEmbedding(Embedding embedding) {
        float[] normalized = normalizeVector(embedding.getOutput());
        return new Embedding(normalized, embedding.getIndex());
    }

    private float[] normalizeVector(float[] vector) {
        if (vector == null || vector.length == 0) {
            return vector;
        }

        double sumOfSquares = 0.0;
        for (float v : vector) {
            sumOfSquares += (double) v * v;
        }

        double magnitude = Math.sqrt(sumOfSquares);
        if (magnitude == 0.0) {
            return vector;
        }

        float[] normalized = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = (float) (vector[i] / magnitude);
        }
        return normalized;
    }

}
