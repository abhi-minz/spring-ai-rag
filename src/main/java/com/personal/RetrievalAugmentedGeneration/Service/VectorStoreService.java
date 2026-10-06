package com.personal.RetrievalAugmentedGeneration.Service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Wraps Spring AI's {@link VectorStore} (pgvector) for storing and
 * retrieving document embeddings.
 */
@Service
public class VectorStoreService {

    private final VectorStore vectorStore;

    public VectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Inserts a single chunk into the vector store.
     *
     * @param content  the text chunk
     * @param metadata arbitrary key-value metadata (e.g. source, page)
     * @return the generated document ID
     */
    public String add(String content, Map<String, Object> metadata) {
        Document doc = new Document(content, metadata);
        vectorStore.add(List.of(doc));
        return doc.getId();
    }

    /**
     * Inserts many chunks in a single batch.
     *
     * @param documents the list of chunks to insert
     * @return the list of generated document IDs
     */
    public List<String> addAll(List<Document> documents) {
        vectorStore.add(documents);
        return documents.stream().map(Document::getId).toList();
    }

    /**
     * Searches the vector store for chunks most similar to the query.
     *
     * @param query the search query
     * @param topK  how many top results to return
     * @return the top-k matching documents, ordered by cosine distance
     */
    public List<Document> search(String query, int topK) {
        return vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(topK).build());
    }

}
