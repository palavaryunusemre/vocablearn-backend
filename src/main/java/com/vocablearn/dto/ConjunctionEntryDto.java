package com.vocablearn.dto;

public record ConjunctionEntryDto(
		Long id,
		int order,
		String englishTerm,
		String turkishMeaning,
		String exampleSentence) {
}
