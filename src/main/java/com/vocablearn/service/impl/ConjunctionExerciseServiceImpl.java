package com.vocablearn.service.impl;

import com.vocablearn.domain.ConjunctionCategory;
import com.vocablearn.domain.ConjunctionEntry;
import com.vocablearn.domain.ConjunctionExerciseAttempt;
import com.vocablearn.domain.ConjunctionMastery;
import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseItemResultDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.exception.ResourceNotFoundException;
import com.vocablearn.exercise.ExerciseGeneratorFactory;
import com.vocablearn.exercise.ExerciseGraderFactory;
import com.vocablearn.repository.AppUserRepository;
import com.vocablearn.repository.ConjunctionCategoryRepository;
import com.vocablearn.repository.ConjunctionEntryRepository;
import com.vocablearn.repository.ConjunctionExerciseAttemptRepository;
import com.vocablearn.repository.ConjunctionMasteryRepository;
import com.vocablearn.service.ConjunctionExerciseService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mirrors {@link ExerciseServiceImpl} exactly, just scoped to
 * {@link ConjunctionEntry}/{@link ConjunctionCategory} instead of
 * {@link com.vocablearn.domain.VocabularyWord}/{@link com.vocablearn.domain.Passage}
 * -- the whole point of {@link Learnable} is that this duplication is
 * confined to the thin service layer (fetch + persist attempt/mastery) while
 * the generator/grader Strategy classes underneath are fully shared.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ConjunctionExerciseServiceImpl implements ConjunctionExerciseService {

	private final ConjunctionEntryRepository conjunctionEntryRepository;
	private final ConjunctionCategoryRepository conjunctionCategoryRepository;
	private final ExerciseGeneratorFactory generatorFactory;
	private final ExerciseGraderFactory graderFactory;
	private final ConjunctionExerciseAttemptRepository conjunctionExerciseAttemptRepository;
	private final ConjunctionMasteryRepository conjunctionMasteryRepository;
	private final AppUserRepository appUserRepository;

	@Override
	@Transactional(readOnly = true)
	public ExerciseDto generate(Long categoryId, ExerciseType type) {
		List<ConjunctionEntry> entries = conjunctionEntryRepository.findByCategoryIdOrderByOrderIndexAsc(categoryId);
		return generatorFactory.get(type).generate(asLearnables(entries));
	}

	@Override
	public ExerciseResultDto submit(
			Long userId, Long categoryId, ExerciseType type, ExerciseSubmissionDto submission) {
		List<ConjunctionEntry> entries = conjunctionEntryRepository.findByCategoryIdOrderByOrderIndexAsc(categoryId);
		ExerciseResultDto result = graderFactory.get(type).grade(asLearnables(entries), submission);

		ConjunctionCategory category = conjunctionCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Conjunction category not found: " + categoryId));

		ConjunctionExerciseAttempt attempt = new ConjunctionExerciseAttempt();
		attempt.setUser(appUserRepository.getReferenceById(userId));
		attempt.setCategory(category);
		attempt.setExerciseType(type);
		attempt.setTotalItems(result.totalCount());
		attempt.setCorrectItems(result.correctCount());
		attempt.setAnsweredAt(Instant.now());
		conjunctionExerciseAttemptRepository.save(attempt);

		updateMastery(userId, entries, result.itemResults());

		return result;
	}

	private void updateMastery(Long userId, List<ConjunctionEntry> entries, List<ExerciseItemResultDto> itemResults) {
		Map<String, ConjunctionEntry> entriesById = entries.stream()
				.collect(Collectors.toMap(e -> e.getId().toString(), Function.identity()));

		for (ExerciseItemResultDto itemResult : itemResults) {
			ConjunctionEntry entry = entriesById.get(itemResult.itemId());
			if (entry == null) {
				continue;
			}
			ConjunctionMastery mastery = conjunctionMasteryRepository
					.findByUserIdAndConjunctionEntryId(userId, entry.getId())
					.orElseGet(() -> {
						ConjunctionMastery created = new ConjunctionMastery();
						created.setUser(appUserRepository.getReferenceById(userId));
						created.setConjunctionEntry(entry);
						return created;
					});
			if (itemResult.correct()) {
				mastery.setCorrectCount(mastery.getCorrectCount() + 1);
			} else {
				mastery.setIncorrectCount(mastery.getIncorrectCount() + 1);
			}
			mastery.setLastPracticedAt(Instant.now());
			conjunctionMasteryRepository.save(mastery);
		}
	}

	private List<Learnable> asLearnables(List<ConjunctionEntry> entries) {
		return entries.stream().map(Learnable.class::cast).toList();
	}
}
