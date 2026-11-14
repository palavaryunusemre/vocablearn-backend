package com.vocablearn.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.vocablearn.domain.ConjunctionEntry;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.exercise.CategoryQuizGenerator;
import com.vocablearn.repository.ConjunctionEntryRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConjunctionCategoryQuizServiceImplTest {

	@Mock
	private ConjunctionEntryRepository conjunctionEntryRepository;

	@Mock
	private CategoryQuizGenerator categoryQuizGenerator;

	@InjectMocks
	private ConjunctionCategoryQuizServiceImpl service;

	@Test
	void generate_delegatesToGeneratorWithAllEntries() {
		List<ConjunctionEntry> entries = List.of(new ConjunctionEntry());
		ExerciseDto expected = ExerciseDto.ofItems("CATEGORY_QUIZ", List.of());
		when(conjunctionEntryRepository.findAll()).thenReturn(entries);
		when(categoryQuizGenerator.generate(entries, 10)).thenReturn(expected);

		ExerciseDto result = service.generate(10);

		assertThat(result).isSameAs(expected);
	}

	@Test
	void submit_delegatesToGenerator() {
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(List.of(), List.of(), List.of());
		ExerciseResultDto expected = new ExerciseResultDto(1, 1, 100, List.of());
		when(categoryQuizGenerator.grade(submission)).thenReturn(expected);

		ExerciseResultDto result = service.submit(submission);

		assertThat(result).isSameAs(expected);
	}
}
