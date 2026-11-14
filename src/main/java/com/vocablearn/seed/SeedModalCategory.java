package com.vocablearn.seed;

import java.util.List;

public record SeedModalCategory(
		int order,
		String title,
		String usageNote,
		List<SeedModalEntry> entries) {
}
