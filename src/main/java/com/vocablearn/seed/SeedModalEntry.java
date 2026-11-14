package com.vocablearn.seed;

public record SeedModalEntry(
		int order,
		String term,
		String meaning,
		String exampleSentence) {
}
