package com.vocablearn.dto;

import com.vocablearn.domain.ExerciseType;
import java.util.List;

public record ConjunctionCategoryDetailDto(
		Long id,
		int order,
		String title,
		String usageNote,
		List<ConjunctionEntryDto> entries,
		List<ExerciseType> availableExerciseTypes) {
}
