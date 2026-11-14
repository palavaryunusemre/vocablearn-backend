package com.vocablearn.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.dto.MatchAnswerDto;
import java.util.List;
import org.junit.jupiter.api.Test;

class MatchingExerciseGraderTest {

	private final MatchingExerciseGrader grader = new MatchingExerciseGrader();

	@Test
	void supports_returnsMatching() {
		assertThat(grader.supports()).isEqualTo(ExerciseType.MATCHING);
	}

	@Test
	void grade_pairsWithMatchingLeftAndRightId_areCorrect() {
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(
				List.of(),
				List.of(new MatchAnswerDto("1", "1"), new MatchAnswerDto("2", "3")),
				List.of());

		ExerciseResultDto result = grader.grade(List.of(), submission);

		assertThat(result.totalCount()).isEqualTo(2);
		assertThat(result.correctCount()).isEqualTo(1);
		assertThat(result.itemResults().get(0).itemId()).isEqualTo("1");
		assertThat(result.itemResults().get(0).correct()).isTrue();
		assertThat(result.itemResults().get(1).correct()).isFalse();
	}
}
