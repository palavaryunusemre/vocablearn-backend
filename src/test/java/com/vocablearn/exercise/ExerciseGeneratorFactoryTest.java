package com.vocablearn.exercise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.exception.BadRequestException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExerciseGeneratorFactoryTest {

	private final ExerciseGeneratorFactory factory = new ExerciseGeneratorFactory(List.of(
			new MultipleChoiceExerciseGenerator(), new FillInBlankExerciseGenerator(),
			new MatchingExerciseGenerator(), new TypingExerciseGenerator()));

	@Test
	void get_returnsTheRegisteredGeneratorForEachType() {
		assertThat(factory.get(ExerciseType.MULTIPLE_CHOICE)).isInstanceOf(MultipleChoiceExerciseGenerator.class);
		assertThat(factory.get(ExerciseType.FILL_BLANK)).isInstanceOf(FillInBlankExerciseGenerator.class);
		assertThat(factory.get(ExerciseType.MATCHING)).isInstanceOf(MatchingExerciseGenerator.class);
		assertThat(factory.get(ExerciseType.TYPING)).isInstanceOf(TypingExerciseGenerator.class);
	}

	@Test
	void get_unregisteredType_throwsBadRequest() {
		ExerciseGeneratorFactory emptyFactory = new ExerciseGeneratorFactory(List.of());

		assertThatThrownBy(() -> emptyFactory.get(ExerciseType.MULTIPLE_CHOICE))
				.isInstanceOf(BadRequestException.class);
	}
}
