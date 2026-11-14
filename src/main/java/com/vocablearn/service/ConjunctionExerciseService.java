package com.vocablearn.service;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;

public interface ConjunctionExerciseService {

	ExerciseDto generate(Long categoryId, ExerciseType type);

	ExerciseResultDto submit(Long userId, Long categoryId, ExerciseType type, ExerciseSubmissionDto submission);
}
