package com.vocablearn.dto;

public record PassageProgressDto(
		Long passageId,
		String title,
		int totalWords,
		int masteredWords,
		int questionsTotal,
		int questionsCorrect) {
}
