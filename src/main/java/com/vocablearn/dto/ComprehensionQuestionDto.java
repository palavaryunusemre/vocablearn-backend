package com.vocablearn.dto;

import java.util.List;

/** Correct answer index is intentionally omitted -- never sent to the client. */
public record ComprehensionQuestionDto(
		Long id,
		int order,
		String questionType,
		String text,
		List<String> options) {
}
