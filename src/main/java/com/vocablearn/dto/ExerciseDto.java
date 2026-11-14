package com.vocablearn.dto;

import java.util.List;

/**
 * Response shape is a discriminated union over {@code exerciseType}:
 * MULTIPLE_CHOICE/FILL_BLANK populate {@code items}; MATCHING populates
 * {@code matchLeftItems}/{@code matchRightItems} instead.
 */
public record ExerciseDto(
		String exerciseType,
		List<ExerciseItemDto> items,
		List<MatchPairItemDto> matchLeftItems,
		List<MatchPairItemDto> matchRightItems) {

	public static ExerciseDto ofItems(String exerciseType, List<ExerciseItemDto> items) {
		return new ExerciseDto(exerciseType, items, null, null);
	}

	public static ExerciseDto ofMatchPairs(
			String exerciseType, List<MatchPairItemDto> left, List<MatchPairItemDto> right) {
		return new ExerciseDto(exerciseType, null, left, right);
	}
}
