package com.vocablearn.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.domain.VocabularyWord;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseItemDto;
import com.vocablearn.dto.ExerciseOptionDto;
import java.util.List;
import org.junit.jupiter.api.Test;

class MultipleChoiceExerciseGeneratorTest {

	private final MultipleChoiceExerciseGenerator generator = new MultipleChoiceExerciseGenerator();

	private VocabularyWord word(long id, String term, String meaning) {
		VocabularyWord word = new VocabularyWord();
		word.setId(id);
		word.setEnglishTerm(term);
		word.setTurkishMeaning(meaning);
		return word;
	}

	@Test
	void supports_returnsMultipleChoice() {
		assertThat(generator.supports()).isEqualTo(ExerciseType.MULTIPLE_CHOICE);
	}

	@Test
	void generate_oneItemPerWord_withPromptAsEnglishTerm() {
		List<Learnable> words = List.of(
				word(1, "phenomenon", "olgu"), word(2, "offer", "teklif etmek"),
				word(3, "victim", "kurban"), word(4, "present", "mevcut"));

		ExerciseDto exercise = generator.generate(words);

		assertThat(exercise.exerciseType()).isEqualTo("MULTIPLE_CHOICE");
		assertThat(exercise.items()).hasSize(4);
		assertThat(exercise.items()).extracting(ExerciseItemDto::prompt)
				.containsExactlyInAnyOrder("phenomenon", "offer", "victim", "present");
	}

	@Test
	void generate_eachItem_hasCorrectOptionAmongDistractors() {
		List<Learnable> words = List.of(
				word(1, "phenomenon", "olgu"), word(2, "offer", "teklif etmek"),
				word(3, "victim", "kurban"), word(4, "present", "mevcut"));

		ExerciseDto exercise = generator.generate(words);

		for (ExerciseItemDto item : exercise.items()) {
			// exactly one option's id equals the item's own id (the correct answer, per the id-equality grading scheme)
			long matchingOptions = item.options().stream()
					.filter(o -> o.optionId().equals(item.itemId()))
					.count();
			assertThat(matchingOptions).isEqualTo(1);
			assertThat(item.options()).hasSizeLessThanOrEqualTo(4);
		}
	}

	@Test
	void generate_withFewerThanFourWords_stillProducesValidOptions() {
		List<Learnable> words = List.of(word(1, "phenomenon", "olgu"), word(2, "offer", "teklif etmek"));

		ExerciseDto exercise = generator.generate(words);

		assertThat(exercise.items()).hasSize(2);
		for (ExerciseItemDto item : exercise.items()) {
			assertThat(item.options()).hasSize(2);
		}
	}

	@Test
	void generate_distractorOptions_useDistractorsOwnMeaning() {
		VocabularyWord phenomenon = word(1, "phenomenon", "olgu");
		VocabularyWord offer = word(2, "offer", "teklif etmek");
		List<Learnable> words = List.of(phenomenon, offer);

		ExerciseDto exercise = generator.generate(words);

		ExerciseItemDto phenomenonItem = exercise.items().stream()
				.filter(i -> i.itemId().equals("1")).findFirst().orElseThrow();
		List<String> optionTexts = phenomenonItem.options().stream().map(ExerciseOptionDto::text).toList();
		assertThat(optionTexts).containsExactlyInAnyOrder("olgu", "teklif etmek");
	}
}
