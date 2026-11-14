package com.vocablearn.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.domain.VocabularyWord;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.MatchPairItemDto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class MatchingExerciseGeneratorTest {

	private final MatchingExerciseGenerator generator = new MatchingExerciseGenerator();

	private VocabularyWord word(long id) {
		VocabularyWord word = new VocabularyWord();
		word.setId(id);
		word.setEnglishTerm("term" + id);
		word.setTurkishMeaning("anlam" + id);
		return word;
	}

	private List<Learnable> asLearnables(List<VocabularyWord> words) {
		return words.stream().map(Learnable.class::cast).toList();
	}

	@Test
	void supports_returnsMatching() {
		assertThat(generator.supports()).isEqualTo(ExerciseType.MATCHING);
	}

	@Test
	void generate_producesEqualSizedLeftAndRightListsSharingIds() {
		List<VocabularyWord> words = IntStream.rangeClosed(1, 5).mapToObj(this::word).toList();

		ExerciseDto exercise = generator.generate(asLearnables(words));

		assertThat(exercise.exerciseType()).isEqualTo("MATCHING");
		assertThat(exercise.matchLeftItems()).hasSize(5);
		assertThat(exercise.matchRightItems()).hasSize(5);

		Set<String> leftIds = exercise.matchLeftItems().stream().map(MatchPairItemDto::id).collect(Collectors.toSet());
		Set<String> rightIds = exercise.matchRightItems().stream().map(MatchPairItemDto::id).collect(Collectors.toSet());
		assertThat(leftIds).isEqualTo(rightIds);
	}

	@Test
	void generate_leftItemsUseEnglishTerm_rightItemsUseTurkishMeaning() {
		VocabularyWord phenomenon = word(1);
		ExerciseDto exercise = generator.generate(List.<Learnable>of(phenomenon));

		assertThat(exercise.matchLeftItems().get(0).text()).isEqualTo("term1");
		assertThat(exercise.matchRightItems().get(0).text()).isEqualTo("anlam1");
	}

	@Test
	void generate_includesEveryWord_evenWellBeyondARoundsWorth() {
		List<VocabularyWord> words = IntStream.rangeClosed(1, 40).mapToObj(this::word).toList();

		ExerciseDto exercise = generator.generate(asLearnables(words));

		assertThat(exercise.matchLeftItems()).hasSize(40);
		assertThat(exercise.matchRightItems()).hasSize(40);
	}

	@Test
	void generate_fewWords_usesAllOfThem() {
		List<VocabularyWord> words = IntStream.rangeClosed(1, 3).mapToObj(this::word).toList();

		ExerciseDto exercise = generator.generate(asLearnables(words));

		assertThat(exercise.matchLeftItems()).hasSize(3);
	}
}
