package com.vocablearn.dto;

public record ModalEntryDto(
		Long id,
		int order,
		String englishTerm,
		String turkishMeaning,
		String exampleSentence) {
}
