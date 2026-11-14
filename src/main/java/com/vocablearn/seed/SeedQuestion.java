package com.vocablearn.seed;

import java.util.List;

/** Field is named "type" (not "questionType") to match passages-seed.json verbatim. */
public record SeedQuestion(
		int order,
		String type,
		String text,
		List<String> options,
		Integer correctOptionIndex) {
}
