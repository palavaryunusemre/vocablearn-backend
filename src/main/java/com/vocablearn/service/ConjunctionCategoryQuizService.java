package com.vocablearn.service;

import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;

public interface ConjunctionCategoryQuizService {

	ExerciseDto generate(int questionCount);

	ExerciseResultDto submit(ExerciseSubmissionDto submission);
}
