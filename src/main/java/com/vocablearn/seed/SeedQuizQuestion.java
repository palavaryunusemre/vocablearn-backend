package com.vocablearn.seed;

public record SeedQuizQuestion(
		int order,
		String examLabel,
		String promptText,
		String turkishTranslation,
		String optionA,
		String optionB,
		String optionC,
		String optionD,
		String optionE,
		String correctOption,
		String explanation) {
}
