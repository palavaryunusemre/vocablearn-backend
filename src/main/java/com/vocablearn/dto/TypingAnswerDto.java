package com.vocablearn.dto;

/** Client's free-text spelling attempt for one TYPING item. */
public record TypingAnswerDto(String itemId, String typedText) {
}
