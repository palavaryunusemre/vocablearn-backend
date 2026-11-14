package com.vocablearn.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.dto.ExerciseAnswerDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import java.util.List;
import org.junit.jupiter.api.Test;

class MultipleChoiceExerciseGraderTest {

	private final MultipleChoiceExerciseGrader grader = new MultipleChoiceExerciseGrader();

	@Test
	void supports_returnsMultipleChoice() {
		assertThat(grader.supports()).isEqualTo(ExerciseType.MULTIPLE_CHOICE);
	}

	@Test
	void grade_matchingItemIdAndOptionId_isCorrect() {
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(
				List.of(new ExerciseAnswerDto("1", "1"), new ExerciseAnswerDto("2", "99")), List.of(), List.of());

		ExerciseResultDto result = grader.grade(List.of(), submission);

		assertThat(result.totalCount()).isEqualTo(2);
		assertThat(result.correctCount()).isEqualTo(1);
		assertThat(result.scorePercentage()).isEqualTo(50);
		assertThat(result.itemResults().get(0).correct()).isTrue();
		assertThat(result.itemResults().get(1).correct()).isFalse();
	}

	@Test
	void grade_emptySubmission_returnsZeroedResultWithoutDivideByZero() {
		ExerciseResultDto result = grader.grade(List.of(), new ExerciseSubmissionDto(List.of(), List.of(), List.of()));

		assertThat(result.totalCount()).isEqualTo(0);
		assertThat(result.correctCount()).isEqualTo(0);
		assertThat(result.scorePercentage()).isEqualTo(0);
	}
}
