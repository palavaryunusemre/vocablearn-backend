package com.vocablearn.dto;

import java.util.List;

public record ProgressDto(
		int totalWords,
		int masteredWords,
		int questionsAnswered,
		int questionsCorrect,
		List<PassageProgressDto> passages) {
}
