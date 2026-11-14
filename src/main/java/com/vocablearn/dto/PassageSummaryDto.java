package com.vocablearn.dto;

public record PassageSummaryDto(
		Long id,
		int order,
		String title,
		String level,
		int vocabularyCount) {
}
