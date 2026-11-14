package com.vocablearn.seed;

public record SeedVocabularyWord(
		int order,
		String englishTerm,
		String turkishMeaning,
		String exampleSentence) {
}
