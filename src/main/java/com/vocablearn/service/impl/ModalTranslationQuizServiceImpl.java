package com.vocablearn.service.impl;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.domain.ModalEntry;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.exercise.ExerciseGeneratorFactory;
import com.vocablearn.exercise.ExerciseGraderFactory;
import com.vocablearn.repository.ModalEntryRepository;
import com.vocablearn.service.ModalTranslationQuizService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reuses the exact same MULTIPLE_CHOICE Strategy classes {@code
 * ConjunctionExerciseService} uses per-category, just fed a random sample
 * drawn from every modal category at once -- grading is id-equality and
 * doesn't care which category an entry came from, so no new grading logic
 * is needed.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModalTranslationQuizServiceImpl implements ModalTranslationQuizService {

	private final ModalEntryRepository modalEntryRepository;
	private final ExerciseGeneratorFactory exerciseGeneratorFactory;
	private final ExerciseGraderFactory exerciseGraderFactory;

	@Override
	public ExerciseDto generate(int questionCount) {
		List<ModalEntry> allEntries = new ArrayList<>(modalEntryRepository.findAll());
		Collections.shuffle(allEntries);
		List<Learnable> sample = new ArrayList<>(allEntries.subList(0, Math.min(questionCount, allEntries.size())));
		return exerciseGeneratorFactory.get(ExerciseType.MULTIPLE_CHOICE).generate(sample);
	}

	@Override
	public ExerciseResultDto submit(ExerciseSubmissionDto submission) {
		return exerciseGraderFactory.get(ExerciseType.MULTIPLE_CHOICE).grade(List.of(), submission);
	}
}
