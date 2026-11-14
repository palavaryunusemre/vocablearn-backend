package com.vocablearn.dto;

/** Client's answer for one MULTIPLE_CHOICE/FILL_BLANK item. */
public record ExerciseAnswerDto(String itemId, String selectedOptionId) {
}
