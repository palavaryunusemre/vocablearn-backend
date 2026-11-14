package com.vocablearn.service;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;

public interface ExerciseService {

	ExerciseDto generate(Long passageId, ExerciseType type);

	ExerciseResultDto submit(Long userId, Long passageId, ExerciseType type, ExerciseSubmissionDto submission);
}
