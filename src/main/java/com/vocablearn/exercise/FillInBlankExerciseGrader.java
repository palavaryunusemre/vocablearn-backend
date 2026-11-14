package com.vocablearn.exercise;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FillInBlankExerciseGrader implements ExerciseGrader {

	@Override
	public ExerciseType supports() {
		return ExerciseType.FILL_BLANK;
	}

	@Override
	public ExerciseResultDto grade(List<Learnable> items, ExerciseSubmissionDto submission) {
		return IdEqualityGrading.gradeAnswers(submission.answers());
	}
}
