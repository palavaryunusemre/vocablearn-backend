package com.vocablearn.dto;

public record ModalCategorySummaryDto(
		Long id,
		int order,
		String title,
		String usageNote,
		int entryCount) {
}
