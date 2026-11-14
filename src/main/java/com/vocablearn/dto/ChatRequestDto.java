package com.vocablearn.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * {@code context} is optional, free-text info about whatever the learner is
 * currently looking at (e.g. the exact quiz question, its options and
 * correct answer) -- sent by the frontend so the chatbot can answer
 * "why is this one right" style questions instead of only the generic
 * grammar-topic PDFs it was ingested with.
 */
public record ChatRequestDto(@NotBlank String question, String context) {
}
