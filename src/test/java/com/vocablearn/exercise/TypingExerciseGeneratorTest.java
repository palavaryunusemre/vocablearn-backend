package com.vocablearn.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.domain.VocabularyWord;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseItemDto;
import java.util.List;
import org.junit.jupiter.api.Test;

class TypingExerciseGeneratorTest {

	private final TypingExerciseGenerator generator = new TypingExerciseGenerator();

	private VocabularyWord word(long id, String term, String meaning) {
		VocabularyWord word = new VocabularyWord();
		word.setId(id);
		word.setEnglishTerm(term);
		word.setTurkishMeaning(meaning);
		return word;
	}

	@Test
	void supports_returnsTyping() {
		assertThat(generator.supports()).isEqualTo(ExerciseType.TYPING);
	}

	@Test
	void generate_oneItemPerWord_withPromptAsTurkishMeaningAndNoOptions() {
		List<Learnable> words = List.of(word(1, "phenomenon", "olgu"), word(2, "offer", "teklif etmek"));

		ExerciseDto exercise = generator.generate(words);

		assertThat(exercise.exerciseType()).isEqualTo("TYPING");
		assertThat(exercise.items()).hasSize(2);
		assertThat(exercise.items()).extracting(ExerciseItemDto::prompt).containsExactlyInAnyOrder("olgu", "teklif etmek");
		assertThat(exercise.items()).allSatisfy(item -> assertThat(item.options()).isEmpty());
	}
}
