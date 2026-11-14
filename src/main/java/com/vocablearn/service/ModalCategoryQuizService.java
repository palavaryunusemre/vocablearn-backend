package com.vocablearn.service;

import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;

/** Cross-category "which modal function does this belong to?" quiz -- see ModalCategoryQuizGenerator. */
public interface ModalCategoryQuizService {

	ExerciseDto generate(int questionCount);

	ExerciseResultDto submit(ExerciseSubmissionDto submission);
}
