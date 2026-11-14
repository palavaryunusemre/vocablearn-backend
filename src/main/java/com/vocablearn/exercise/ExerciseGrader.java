package com.vocablearn.exercise;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import java.util.List;

/** Strategy interface: one implementation per {@link ExerciseType}, selected by {@link ExerciseGraderFactory}. */
public interface ExerciseGrader {

	ExerciseType supports();

	ExerciseResultDto grade(List<Learnable> items, ExerciseSubmissionDto submission);
}
