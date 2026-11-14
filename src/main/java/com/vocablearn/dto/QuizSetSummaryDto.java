package com.vocablearn.dto;

public record QuizSetSummaryDto(
		Long id,
		int order,
		String title,
		String description,
		int questionCount) {
}
