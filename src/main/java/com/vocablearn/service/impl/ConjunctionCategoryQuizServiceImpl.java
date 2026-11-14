package com.vocablearn.service.impl;

import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.exercise.CategoryQuizGenerator;
import com.vocablearn.repository.ConjunctionEntryRepository;
import com.vocablearn.service.ConjunctionCategoryQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * No attempt/mastery persistence here, unlike {@link ExerciseServiceImpl} --
 * this cross-category quiz is a practice tool rather than something tied to
 * a single passage/category's progress tracking schema.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConjunctionCategoryQuizServiceImpl implements ConjunctionCategoryQuizService {

	private final ConjunctionEntryRepository conjunctionEntryRepository;
	private final CategoryQuizGenerator categoryQuizGenerator;

	@Override
	public ExerciseDto generate(int questionCount) {
		return categoryQuizGenerator.generate(conjunctionEntryRepository.findAll(), questionCount);
	}

	@Override
	public ExerciseResultDto submit(ExerciseSubmissionDto submission) {
		return categoryQuizGenerator.grade(submission);
	}
}
