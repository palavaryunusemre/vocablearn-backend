package com.vocablearn.exercise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.exception.BadRequestException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExerciseGraderFactoryTest {

	private final ExerciseGraderFactory factory = new ExerciseGraderFactory(List.of(
			new MultipleChoiceExerciseGrader(), new FillInBlankExerciseGrader(),
			new MatchingExerciseGrader(), new TypingExerciseGrader()));

	@Test
	void get_returnsTheRegisteredGraderForEachType() {
		assertThat(factory.get(ExerciseType.MULTIPLE_CHOICE)).isInstanceOf(MultipleChoiceExerciseGrader.class);
		assertThat(factory.get(ExerciseType.FILL_BLANK)).isInstanceOf(FillInBlankExerciseGrader.class);
		assertThat(factory.get(ExerciseType.MATCHING)).isInstanceOf(MatchingExerciseGrader.class);
		assertThat(factory.get(ExerciseType.TYPING)).isInstanceOf(TypingExerciseGrader.class);
	}

	@Test
	void get_unregisteredType_throwsBadRequest() {
		ExerciseGraderFactory emptyFactory = new ExerciseGraderFactory(List.of());

		assertThatThrownBy(() -> emptyFactory.get(ExerciseType.MATCHING)).isInstanceOf(BadRequestException.class);
	}
}
