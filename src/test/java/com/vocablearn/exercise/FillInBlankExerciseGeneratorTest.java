package com.vocablearn.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.VocabularyWord;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseItemDto;
import java.util.List;
import org.junit.jupiter.api.Test;

class FillInBlankExerciseGeneratorTest {

	private final FillInBlankExerciseGenerator generator = new FillInBlankExerciseGenerator();

	private VocabularyWord word(long id, String term, String example) {
		VocabularyWord word = new VocabularyWord();
		word.setId(id);
		word.setEnglishTerm(term);
		word.setTurkishMeaning("anlam" + id);
		word.setExampleSentence(example);
		return word;
	}

	@Test
	void supports_returnsFillBlank() {
		assertThat(generator.supports()).isEqualTo(ExerciseType.FILL_BLANK);
	}

	@Test
	void generate_blanksOutTheTermInItsExampleSentence() {
		VocabularyWord phenomenon = word(1, "phenomenon",
				"The bystander effect is a psychological phenomenon where individuals act differently.");
		VocabularyWord offer = word(2, "offer", "They offer help when needed.");

		ExerciseDto exercise = generator.generate(List.of(phenomenon, offer));

		ExerciseItemDto item = exercise.items().stream().filter(i -> i.itemId().equals("1")).findFirst().orElseThrow();
		assertThat(item.prompt()).contains("_____");
		assertThat(item.prompt()).doesNotContain("phenomenon");
	}

	@Test
	void generate_wordsWithoutExampleSentence_areExcluded() {
		VocabularyWord withExample = word(1, "phenomenon", "This is a phenomenon worth noting.");
		VocabularyWord withoutExample = word(2, "offer", null);
		VocabularyWord blankExample = word(3, "victim", "   ");

		ExerciseDto exercise = generator.generate(List.of(withExample, withoutExample, blankExample));

		assertThat(exercise.items()).hasSize(1);
		assertThat(exercise.items().get(0).itemId()).isEqualTo("1");
	}

	@Test
	void generate_termNotFoundInOwnSentence_isSkipped() {
		// example sentence doesn't actually contain the term -- can't be blanked, must be dropped
		VocabularyWord mismatched = word(1, "phenomenon", "This sentence talks about something else entirely.");

		ExerciseDto exercise = generator.generate(List.of(mismatched));

		assertThat(exercise.items()).isEmpty();
	}

	@Test
	void generate_multiWordTerm_usesPrimaryFormBeforeSlash() {
		VocabularyWord phrasal = word(1, "intervene in/with",
				"Nobody chose to intervene in the situation that day.");

		ExerciseDto exercise = generator.generate(List.of(phrasal));

		assertThat(exercise.items()).hasSize(1);
		assertThat(exercise.items().get(0).prompt()).contains("_____");
	}

	@Test
	void generate_noEligibleWords_returnsEmptyItemList() {
		ExerciseDto exercise = generator.generate(List.of());

		assertThat(exercise.items()).isEmpty();
		assertThat(exercise.exerciseType()).isEqualTo("FILL_BLANK");
	}
}
