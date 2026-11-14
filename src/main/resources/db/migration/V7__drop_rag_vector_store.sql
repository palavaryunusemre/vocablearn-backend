-- The chatbot no longer uses RAG/retrieval -- it answers directly from the
-- Groq-backed chat model (see ChatServiceImpl), so the vector store created
-- in V5 is now dead weight. V5 itself is left in place (never delete an
-- already-applied migration -- Flyway validates applied history against the
-- files on disk and would fail to start otherwise); this just undoes it.
DROP INDEX IF EXISTS vector_store_embedding_idx;
DROP TABLE IF EXISTS vector_store;
