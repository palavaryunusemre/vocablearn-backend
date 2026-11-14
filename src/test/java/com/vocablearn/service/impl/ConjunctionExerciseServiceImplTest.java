package com.vocablearn.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vocablearn.domain.ConjunctionCategory;
import com.vocablearn.domain.ConjunctionEntry;
import com.vocablearn.domain.ConjunctionMastery;
import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseItemResultDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.exercise.ExerciseGenerator;
import com.vocablearn.exercise.ExerciseGeneratorFactory;
import com.vocablearn.exercise.ExerciseGrader;
import com.vocablearn.exercise.ExerciseGraderFactory;
import com.vocablearn.repository.AppUserRepository;
import com.vocablearn.repository.ConjunctionCategoryRepository;
import com.vocablearn.repository.ConjunctionEntryRepository;
import com.vocablearn.repository.ConjunctionExerciseAttemptRepository;
import com.vocablearn.repository.ConjunctionMasteryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConjunctionExerciseServiceImplTest {

	@Mock
	private ConjunctionEntryRepository conjunctionEntryRepository;

	@Mock
	private ConjunctionCategoryRepository conjunctionCategoryRepository;

	@Mock
	private ExerciseGeneratorFactory generatorFactory;

	@Mock
	private ExerciseGraderFactory graderFactory;

	@Mock
	private ConjunctionExerciseAttemptRepository conjunctionExerciseAttemptRepository;

	@Mock
	private ConjunctionMasteryRepository conjunctionMasteryRepository;

	@Mock
	private AppUserRepository appUserRepository;

	@Mock
	private ExerciseGenerator generator;

	@Mock
	private ExerciseGrader grader;

	@InjectMocks
	private ConjunctionExerciseServiceImpl exerciseService;

	private ConjunctionEntry entry(long id) {
		ConjunctionEntry entry = new ConjunctionEntry();
		entry.setId(id);
		entry.setEnglishTerm("term" + id);
		entry.setTurkishMeaning("anlam" + id);
		return entry;
	}

	@Test
	void generate_delegatesToGeneratorFactory() {
		List<ConjunctionEntry> entries = List.of(entry(1L), entry(2L));
		ExerciseDto expected = ExerciseDto.ofItems("MULTIPLE_CHOICE", List.of());
		when(conjunctionEntryRepository.findByCategoryIdOrderByOrderIndexAsc(5L)).thenReturn(entries);
		when(generatorFactory.get(ExerciseType.MULTIPLE_CHOICE)).thenReturn(generator);
		when(generator.generate(List.<Learnable>copyOf(entries))).thenReturn(expected);

		ExerciseDto result = exerciseService.generate(5L, ExerciseType.MULTIPLE_CHOICE);

		assertThat(result).isSameAs(expected);
	}

	@Test
	void submit_savesAttemptAndCreatesNewMasteryOnFirstAnswer() {
		ConjunctionEntry entry1 = entry(1L);
		List<ConjunctionEntry> entries = List.of(entry1);
		ConjunctionCategory category = new ConjunctionCategory();
		category.setId(5L);
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(List.of(), List.of(), List.of());
		ExerciseResultDto result = new ExerciseResultDto(
				1, 1, 100, List.of(new ExerciseItemResultDto("1", true, "1")));

		when(conjunctionEntryRepository.findByCategoryIdOrderByOrderIndexAsc(5L)).thenReturn(entries);
		when(graderFactory.get(ExerciseType.MULTIPLE_CHOICE)).thenReturn(grader);
		when(grader.grade(List.<Learnable>copyOf(entries), submission)).thenReturn(result);
		when(conjunctionCategoryRepository.findById(5L)).thenReturn(Optional.of(category));
		when(conjunctionMasteryRepository.findByUserIdAndConjunctionEntryId(7L, 1L)).thenReturn(Optional.empty());

		ExerciseResultDto actual = exerciseService.submit(7L, 5L, ExerciseType.MULTIPLE_CHOICE, submission);

		assertThat(actual).isSameAs(result);
		verify(conjunctionExerciseAttemptRepository).save(any());

		ArgumentCaptor<ConjunctionMastery> masteryCaptor = ArgumentCaptor.forClass(ConjunctionMastery.class);
		verify(conjunctionMasteryRepository).save(masteryCaptor.capture());
		ConjunctionMastery saved = masteryCaptor.getValue();
		assertThat(saved.getCorrectCount()).isEqualTo(1);
		assertThat(saved.getIncorrectCount()).isEqualTo(0);
		assertThat(saved.getConjunctionEntry()).isSameAs(entry1);
	}

	@Test
	void submit_incrementsExistingMasteryOnIncorrectAnswer() {
		ConjunctionEntry entry1 = entry(1L);
		List<ConjunctionEntry> entries = List.of(entry1);
		ConjunctionCategory category = new ConjunctionCategory();
		category.setId(5L);
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(List.of(), List.of(), List.of());
		ExerciseResultDto result = new ExerciseResultDto(
				0, 1, 0, List.of(new ExerciseItemResultDto("1", false, "1")));

		ConjunctionMastery existing = new ConjunctionMastery();
		existing.setCorrectCount(2);
		existing.setIncorrectCount(1);

		when(conjunctionEntryRepository.findByCategoryIdOrderByOrderIndexAsc(5L)).thenReturn(entries);
		when(graderFactory.get(ExerciseType.FILL_BLANK)).thenReturn(grader);
		when(grader.grade(List.<Learnable>copyOf(entries), submission)).thenReturn(result);
		when(conjunctionCategoryRepository.findById(5L)).thenReturn(Optional.of(category));
		when(conjunctionMasteryRepository.findByUserIdAndConjunctionEntryId(7L, 1L)).thenReturn(Optional.of(existing));

		exerciseService.submit(7L, 5L, ExerciseType.FILL_BLANK, submission);

		assertThat(existing.getCorrectCount()).isEqualTo(2);
		assertThat(existing.getIncorrectCount()).isEqualTo(2);
		verify(conjunctionMasteryRepository).save(existing);
	}

	@Test
	void submit_itemResultWithUnknownItemId_isIgnoredForMastery() {
		List<ConjunctionEntry> entries = List.of(entry(1L));
		ConjunctionCategory category = new ConjunctionCategory();
		category.setId(5L);
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(List.of(), List.of(), List.of());
		ExerciseResultDto result = new ExerciseResultDto(
				0, 1, 0, List.of(new ExerciseItemResultDto("does-not-exist", false, "x")));

		when(conjunctionEntryRepository.findByCategoryIdOrderByOrderIndexAsc(5L)).thenReturn(entries);
		when(graderFactory.get(ExerciseType.MATCHING)).thenReturn(grader);
		when(grader.grade(List.<Learnable>copyOf(entries), submission)).thenReturn(result);
		when(conjunctionCategoryRepository.findById(5L)).thenReturn(Optional.of(category));

		exerciseService.submit(7L, 5L, ExerciseType.MATCHING, submission);

		verify(conjunctionMasteryRepository, never()).save(any());
		verify(conjunctionMasteryRepository, never()).findByUserIdAndConjunctionEntryId(any(), any());
	}
}
