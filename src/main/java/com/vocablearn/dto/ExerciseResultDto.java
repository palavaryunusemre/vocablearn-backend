package com.vocablearn.dto;

import java.util.List;

public record ExerciseResultDto(
		int correctCount,
		int totalCount,
		int scorePercentage,
		List<ExerciseItemResultDto> itemResults) {
}
