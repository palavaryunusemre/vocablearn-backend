package com.vocablearn.dto;

import java.util.List;

/** itemId equals the VocabularyWord's id being tested; used by MULTIPLE_CHOICE and FILL_BLANK. */
public record ExerciseItemDto(String itemId, String prompt, List<ExerciseOptionDto> options) {
}
