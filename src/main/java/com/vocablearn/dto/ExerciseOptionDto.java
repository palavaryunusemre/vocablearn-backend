package com.vocablearn.dto;

/** optionId equals the owning VocabularyWord's id -- see ExerciseGrader for why this makes grading trivial. */
public record ExerciseOptionDto(String optionId, String text) {
}
