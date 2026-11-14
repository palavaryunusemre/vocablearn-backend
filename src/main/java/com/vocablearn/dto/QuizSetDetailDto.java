package com.vocablearn.dto;

import java.util.List;

public record QuizSetDetailDto(
		Long id,
		int order,
		String title,
		String description,
		List<QuizQuestionDto> questions) {
}
