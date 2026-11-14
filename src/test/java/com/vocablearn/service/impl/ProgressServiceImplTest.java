package com.vocablearn.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.vocablearn.domain.ComprehensionQuestion;
import com.vocablearn.domain.Passage;
import com.vocablearn.domain.QuestionAttempt;
import com.vocablearn.domain.VocabularyWord;
import com.vocablearn.domain.WordMastery;
import com.vocablearn.dto.PassageProgressDto;
import com.vocablearn.dto.ProgressDto;
import com.vocablearn.repository.PassageRepository;
import com.vocablearn.repository.QuestionAttemptRepository;
import com.vocablearn.repository.VocabularyWordRepository;
import com.vocablearn.repository.WordMasteryRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProgressServiceImplTest {

	@Mock
	private PassageRepository passageRepository;

	@Mock
	private VocabularyWordRepository vocabularyWordRepository;

	@Mock
	private WordMasteryRepository wordMasteryRepository;

	@Mock
	private QuestionAttemptRepository questionAttemptRepository;

	@InjectMocks
	private ProgressServiceImpl progressService;

	private Passage passage(long id, String title) {
		Passage passage = new Passage();
		passage.setId(id);
		passage.setTitle(title);
		return passage;
	}

	private WordMastery mastery(Passage passage, int correctCount) {
		VocabularyWord word = new VocabularyWord();
		word.setPassage(passage);
		WordMastery mastery = new WordMastery();
		mastery.setVocabularyWord(word);
		mastery.setCorrectCount(correctCount);
		return mastery;
	}

	private QuestionAttempt attempt(Passage passage, boolean correct) {
		ComprehensionQuestion question = new ComprehensionQuestion();
		question.setPassage(passage);
		QuestionAttempt attempt = new QuestionAttempt();
		attempt.setQuestion(question);
		attempt.setCorrect(correct);
		return attempt;
	}

	@Test
	void getProgress_aggregatesWordMasteryAboveThresholdPerPassage() {
		Passage passage1 = passage(1L, "Passage One");
		Passage passage2 = passage(2L, "Passage Two");
		when(passageRepository.findAllByOrderByOrderIndexAsc()).thenReturn(List.of(passage1, passage2));
		when(vocabularyWordRepository.countByPassageId(1L)).thenReturn(10L);
		when(vocabularyWordRepository.countByPassageId(2L)).thenReturn(5L);
		// mastery threshold is 3: only the first two count as mastered for passage1, the passage2 one (count=1) doesn't.
		when(wordMasteryRepository.findByUserId(99L)).thenReturn(List.of(
				mastery(passage1, 3), mastery(passage1, 5), mastery(passage1, 1), mastery(passage2, 1)));
		when(questionAttemptRepository.findByUserId(99L)).thenReturn(List.of());

		ProgressDto progress = progressService.getProgress(99L);

		assertThat(progress.totalWords()).isEqualTo(15);
		assertThat(progress.masteredWords()).isEqualTo(2);

		PassageProgressDto passage1Progress = progress.passages().get(0);
		assertThat(passage1Progress.totalWords()).isEqualTo(10);
		assertThat(passage1Progress.masteredWords()).isEqualTo(2);

		PassageProgressDto passage2Progress = progress.passages().get(1);
		assertThat(passage2Progress.masteredWords()).isEqualTo(0);
	}

	@Test
	void getProgress_aggregatesQuestionAttemptsPerPassageAndOverall() {
		Passage passage1 = passage(1L, "Passage One");
		Passage passage2 = passage(2L, "Passage Two");
		when(passageRepository.findAllByOrderByOrderIndexAsc()).thenReturn(List.of(passage1, passage2));
		when(vocabularyWordRepository.countByPassageId(1L)).thenReturn(0L);
		when(vocabularyWordRepository.countByPassageId(2L)).thenReturn(0L);
		when(wordMasteryRepository.findByUserId(99L)).thenReturn(List.of());
		when(questionAttemptRepository.findByUserId(99L)).thenReturn(List.of(
				attempt(passage1, true), attempt(passage1, false), attempt(passage2, true)));

		ProgressDto progress = progressService.getProgress(99L);

		assertThat(progress.questionsAnswered()).isEqualTo(3);
		assertThat(progress.questionsCorrect()).isEqualTo(2);
		assertThat(progress.passages().get(0).questionsTotal()).isEqualTo(2);
		assertThat(progress.passages().get(0).questionsCorrect()).isEqualTo(1);
		assertThat(progress.passages().get(1).questionsTotal()).isEqualTo(1);
		assertThat(progress.passages().get(1).questionsCorrect()).isEqualTo(1);
	}

	@Test
	void getProgress_noActivity_returnsZeroedButPresentPassages() {
		Passage passage1 = passage(1L, "Passage One");
		when(passageRepository.findAllByOrderByOrderIndexAsc()).thenReturn(List.of(passage1));
		when(vocabularyWordRepository.countByPassageId(1L)).thenReturn(20L);
		when(wordMasteryRepository.findByUserId(99L)).thenReturn(List.of());
		when(questionAttemptRepository.findByUserId(99L)).thenReturn(List.of());

		ProgressDto progress = progressService.getProgress(99L);

		assertThat(progress.passages()).hasSize(1);
		assertThat(progress.passages().get(0).totalWords()).isEqualTo(20);
		assertThat(progress.passages().get(0).masteredWords()).isEqualTo(0);
		assertThat(progress.questionsAnswered()).isEqualTo(0);
	}
}
