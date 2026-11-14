package com.vocablearn.seed;

import java.util.List;

public record SeedPassage(
		int order,
		String title,
		String level,
		List<SeedParagraph> paragraphs,
		List<SeedVocabularyWord> vocabulary,
		List<SeedQuestion> questions) {
}
