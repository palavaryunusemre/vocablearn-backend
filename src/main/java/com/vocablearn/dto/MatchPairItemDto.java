package com.vocablearn.dto;

/** id equals the VocabularyWord's id; the matching game pairs a left item and right item sharing the same id. */
public record MatchPairItemDto(String id, String text) {
}
