package com.vocablearn.dto;

public record QuestionResultDto(
		Long questionId,
		boolean correct,
		int selectedOptionIndex,
		int correctOptionIndex) {
}
