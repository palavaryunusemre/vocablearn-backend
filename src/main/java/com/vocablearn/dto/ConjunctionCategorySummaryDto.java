package com.vocablearn.dto;

public record ConjunctionCategorySummaryDto(
		Long id,
		int order,
		String title,
		String usageNote,
		int entryCount) {
}
