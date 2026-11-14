package com.vocablearn.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.dto.ExerciseAnswerDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import java.util.List;
import org.junit.jupiter.api.Test;

class FillInBlankExerciseGraderTest {

	private final FillInBlankExerciseGrader grader = new FillInBlankExerciseGrader();

	@Test
	void supports_returnsFillBlank() {
		assertThat(grader.supports()).isEqualTo(ExerciseType.FILL_BLANK);
	}

	@Test
	void grade_allCorrect_hundredPercent() {
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(
				List.of(new ExerciseAnswerDto("1", "1"), new ExerciseAnswerDto("2", "2")), List.of(), List.of());

		ExerciseResultDto result = grader.grade(List.of(), submission);

		assertThat(result.correctCount()).isEqualTo(2);
		assertThat(result.scorePercentage()).isEqualTo(100);
	}

	@Test
	void grade_allIncorrect_zeroPercent() {
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(
				List.of(new ExerciseAnswerDto("1", "2"), new ExerciseAnswerDto("2", "1")), List.of(), List.of());

		ExerciseResultDto result = grader.grade(List.of(), submission);

		assertThat(result.correctCount()).isEqualTo(0);
		assertThat(result.scorePercentage()).isEqualTo(0);
	}
}
