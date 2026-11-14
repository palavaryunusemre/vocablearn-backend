package com.vocablearn.dto;

/** Client's proposed pairing for one MATCHING item. */
public record MatchAnswerDto(String leftId, String rightId) {
}
