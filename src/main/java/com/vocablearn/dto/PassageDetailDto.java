package com.vocablearn.dto;

import java.util.List;

public record PassageDetailDto(
		Long id,
		int order,
		String title,
		String level,
		List<ParagraphDto> paragraphs,
		List<VocabularyWordDto> vocabulary,
		List<ComprehensionQuestionDto> questions) {
}
