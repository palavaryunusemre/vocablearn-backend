package com.vocablearn.seed;

import java.util.List;

public record SeedConjunctionCategory(
		int order,
		String title,
		String translationNote,
		String usageNote,
		List<SeedConjunctionEntry> entries) {
}
