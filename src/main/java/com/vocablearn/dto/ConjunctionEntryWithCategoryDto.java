package com.vocablearn.dto;

/** Flat entry + its category's title, for the "browse all conjunctions in sequence" card view. */
public record ConjunctionEntryWithCategoryDto(
		Long id,
		String englishTerm,
		String turkishMeaning,
		String exampleSentence,
		Long categoryId,
		String categoryTitle) {
}
