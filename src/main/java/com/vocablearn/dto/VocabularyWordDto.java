package com.vocablearn.dto;

public record VocabularyWordDto(
		Long id,
		int order,
		String englishTerm,
		String turkishMeaning,
		String exampleSentence) {
}
