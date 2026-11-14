package com.vocablearn.seed;

import java.util.List;

public record SeedQuizSet(
		int order,
		String title,
		String description,
		List<SeedQuizQuestion> questions) {
}
