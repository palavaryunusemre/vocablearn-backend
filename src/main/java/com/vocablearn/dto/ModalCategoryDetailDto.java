package com.vocablearn.dto;

import java.util.List;

public record ModalCategoryDetailDto(
		Long id,
		int order,
		String title,
		String usageNote,
		List<ModalEntryDto> entries) {
}
