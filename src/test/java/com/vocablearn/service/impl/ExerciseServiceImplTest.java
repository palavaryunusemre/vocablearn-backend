package com.vocablearn.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.domain.Passage;
import com.vocablearn.domain.VocabularyWord;
import com.vocablearn.domain.WordMastery;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseItemResultDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.exercise.ExerciseGenerator;
import com.vocablearn.exercise.ExerciseGeneratorFactory;
import com.vocablearn.exercise.ExerciseGrader;
import com.vocablearn.exercise.ExerciseGraderFactory;
import com.vocablearn.repository.AppUserRepository;
import com.vocablearn.repository.ExerciseAttemptRepository;
import com.vocablearn.repository.PassageRepository;
import com.vocablearn.repository.VocabularyWordRepository;
import com.vocablearn.repository.WordMasteryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceImplTest {

	@Mock
	private VocabularyWordRepository vocabularyWordRepository;

	@Mock
	private PassageRepository passageRepository;

	@Mock
	private ExerciseGeneratorFactory generatorFactory;

	@Mock
	private ExerciseGraderFactory graderFactory;

	@Mock
	private ExerciseAttemptRepository exerciseAttemptRepository;

	@Mock
	private WordMasteryRepository wordMasteryRepository;

	@Mock
	private AppUserRepository appUserRepository;

	@Mock
	private ExerciseGenerator generator;

	@Mock
	private ExerciseGrader grader;

	@InjectMocks
	private ExerciseServiceImpl exerciseService;

	private VocabularyWord word(long id) {
		VocabularyWord word = new VocabularyWord();
		word.setId(id);
		word.setEnglishTerm("term" + id);
		word.setTurkishMeaning("anlam" + id);
		return word;
	}

	@Test
	void generate_delegatesToGeneratorFactory() {
		List<VocabularyWord> words = List.of(word(1L), word(2L));
		ExerciseDto expected = ExerciseDto.ofItems("MULTIPLE_CHOICE", List.of());
		when(vocabularyWordRepository.findByPassageIdOrderByOrderIndexAsc(5L)).thenReturn(words);
		when(generatorFactory.get(ExerciseType.MULTIPLE_CHOICE)).thenReturn(generator);
		when(generator.generate(List.<Learnable>copyOf(words))).thenReturn(expected);

		ExerciseDto result = exerciseService.generate(5L, ExerciseType.MULTIPLE_CHOICE);

		assertThat(result).isSameAs(expected);
	}

	@Test
	void submit_savesAttemptAndCreatesNewWordMasteryOnFirstAnswer() {
		VocabularyWord word1 = word(1L);
		List<VocabularyWord> words = List.of(word1);
		Passage passage = new Passage();
		passage.setId(5L);
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(List.of(), List.of(), List.of());
		ExerciseResultDto result = new ExerciseResultDto(
				1, 1, 100, List.of(new ExerciseItemResultDto("1", true, "1")));

		when(vocabularyWordRepository.findByPassageIdOrderByOrderIndexAsc(5L)).thenReturn(words);
		when(graderFactory.get(ExerciseType.MULTIPLE_CHOICE)).thenReturn(grader);
		when(grader.grade(List.<Learnable>copyOf(words), submission)).thenReturn(result);
		when(passageRepository.findById(5L)).thenReturn(Optional.of(passage));
		when(wordMasteryRepository.findByUserIdAndVocabularyWordId(7L, 1L)).thenReturn(Optional.empty());

		ExerciseResultDto actual = exerciseService.submit(7L, 5L, ExerciseType.MULTIPLE_CHOICE, submission);

		assertThat(actual).isSameAs(result);
		verify(exerciseAttemptRepository).save(any());

		ArgumentCaptor<WordMastery> masteryCaptor = ArgumentCaptor.forClass(WordMastery.class);
		verify(wordMasteryRepository).save(masteryCaptor.capture());
		WordMastery saved = masteryCaptor.getValue();
		assertThat(saved.getCorrectCount()).isEqualTo(1);
		assertThat(saved.getIncorrectCount()).isEqualTo(0);
		assertThat(saved.getVocabularyWord()).isSameAs(word1);
	}

	@Test
	void submit_incrementsExistingWordMasteryOnIncorrectAnswer() {
		VocabularyWord word1 = word(1L);
		List<VocabularyWord> words = List.of(word1);
		Passage passage = new Passage();
		passage.setId(5L);
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(List.of(), List.of(), List.of());
		ExerciseResultDto result = new ExerciseResultDto(
				0, 1, 0, List.of(new ExerciseItemResultDto("1", false, "1")));

		WordMastery existing = new WordMastery();
		existing.setCorrectCount(2);
		existing.setIncorrectCount(1);

		when(vocabularyWordRepository.findByPassageIdOrderByOrderIndexAsc(5L)).thenReturn(words);
		when(graderFactory.get(ExerciseType.FILL_BLANK)).thenReturn(grader);
		when(grader.grade(List.<Learnable>copyOf(words), submission)).thenReturn(result);
		when(passageRepository.findById(5L)).thenReturn(Optional.of(passage));
		when(wordMasteryRepository.findByUserIdAndVocabularyWordId(7L, 1L)).thenReturn(Optional.of(existing));

		exerciseService.submit(7L, 5L, ExerciseType.FILL_BLANK, submission);

		assertThat(existing.getCorrectCount()).isEqualTo(2);
		assertThat(existing.getIncorrectCount()).isEqualTo(2);
		verify(wordMasteryRepository).save(existing);
	}

	@Test
	void submit_itemResultWithUnknownItemId_isIgnoredForMastery() {
		List<VocabularyWord> words = List.of(word(1L));
		Passage passage = new Passage();
		passage.setId(5L);
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(List.of(), List.of(), List.of());
		ExerciseResultDto result = new ExerciseResultDto(
				0, 1, 0, List.of(new ExerciseItemResultDto("does-not-exist", false, "x")));

		when(vocabularyWordRepository.findByPassageIdOrderByOrderIndexAsc(5L)).thenReturn(words);
		when(graderFactory.get(ExerciseType.MATCHING)).thenReturn(grader);
		when(grader.grade(List.<Learnable>copyOf(words), submission)).thenReturn(result);
		when(passageRepository.findById(5L)).thenReturn(Optional.of(passage));

		exerciseService.submit(7L, 5L, ExerciseType.MATCHING, submission);

		verify(wordMasteryRepository, never()).save(any());
		verify(wordMasteryRepository, never()).findByUserIdAndVocabularyWordId(any(), any());
	}
}
