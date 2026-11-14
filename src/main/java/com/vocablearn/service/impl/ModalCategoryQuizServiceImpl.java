package com.vocablearn.service.impl;

import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.exercise.ModalCategoryQuizGenerator;
import com.vocablearn.repository.ModalEntryRepository;
import com.vocablearn.service.ModalCategoryQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModalCategoryQuizServiceImpl implements ModalCategoryQuizService {

	private final ModalEntryRepository modalEntryRepository;
	private final ModalCategoryQuizGenerator modalCategoryQuizGenerator;

	@Override
	public ExerciseDto generate(int questionCount) {
		return modalCategoryQuizGenerator.generate(modalEntryRepository.findAll(), questionCount);
	}

	@Override
	public ExerciseResultDto submit(ExerciseSubmissionDto submission) {
		return modalCategoryQuizGenerator.grade(submission);
	}
}
