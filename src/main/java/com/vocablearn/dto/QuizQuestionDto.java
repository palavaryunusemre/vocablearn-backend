package com.vocablearn.dto;

import java.util.List;

/**
 * {@code correctOption} is sent directly (unlike the other exercise DTOs,
 * which hide the answer behind a submit/grade round-trip) -- this is a
 * personal self-study tool with client-side-only grading and progressive
 * hints, not a scored assessment that needs to resist a motivated cheater.
 */
public record QuizQuestionDto(
		Long id,
		int order,
		String examLabel,
		String promptText,
		String turkishTranslation,
		List<QuizOptionDto> options,
		String correctOption,
		String explanation) {
}
