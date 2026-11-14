package com.vocablearn.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.domain.VocabularyWord;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.dto.TypingAnswerDto;
import java.util.List;
import org.junit.jupiter.api.Test;

class TypingExerciseGraderTest {

	private final TypingExerciseGrader grader = new TypingExerciseGrader();

	private VocabularyWord word(long id, String term) {
		VocabularyWord word = new VocabularyWord();
		word.setId(id);
		word.setEnglishTerm(term);
		return word;
	}

	@Test
	void supports_returnsTyping() {
		assertThat(grader.supports()).isEqualTo(ExerciseType.TYPING);
	}

	@Test
	void grade_exactMatch_isCorrect() {
		List<Learnable> words = List.of(word(1, "phenomenon"));
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(
				List.of(), List.of(), List.of(new TypingAnswerDto("1", "phenomenon")));

		ExerciseResultDto result = grader.grade(words, submission);

		assertThat(result.correctCount()).isEqualTo(1);
		assertThat(result.itemResults().get(0).correct()).isTrue();
		assertThat(result.itemResults().get(0).correctOptionId()).isEqualTo("phenomenon");
	}

	@Test
	void grade_caseInsensitiveAndTrimmed_isStillCorrect() {
		List<Learnable> words = List.of(word(1, "phenomenon"));
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(
				List.of(), List.of(), List.of(new TypingAnswerDto("1", "  PHENOMENON  ")));

		ExerciseResultDto result = grader.grade(words, submission);

		assertThat(result.itemResults().get(0).correct()).isTrue();
	}

	@Test
	void grade_misspelled_isIncorrectButRevealsCorrectSpelling() {
		List<Learnable> words = List.of(word(1, "phenomenon"));
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(
				List.of(), List.of(), List.of(new TypingAnswerDto("1", "phenomenan")));

		ExerciseResultDto result = grader.grade(words, submission);

		assertThat(result.correctCount()).isEqualTo(0);
		assertThat(result.itemResults().get(0).correct()).isFalse();
		assertThat(result.itemResults().get(0).correctOptionId()).isEqualTo("phenomenon");
	}

	@Test
	void grade_multiWordAlternateForm_usesPrimaryTermBeforeSlash() {
		List<Learnable> words = List.of(word(1, "intervene in/with"));
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(
				List.of(), List.of(), List.of(new TypingAnswerDto("1", "intervene in")));

		ExerciseResultDto result = grader.grade(words, submission);

		assertThat(result.itemResults().get(0).correct()).isTrue();
		assertThat(result.itemResults().get(0).correctOptionId()).isEqualTo("intervene in");
	}
}
