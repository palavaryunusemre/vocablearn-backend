package com.vocablearn.dto;

import java.util.List;

public record QuestionSubmissionResultDto(
		List<QuestionResultDto> results,
		int correctCount,
		int totalCount) {
}
