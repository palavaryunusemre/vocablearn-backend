package com.vocablearn.service.impl;

import com.vocablearn.domain.ExerciseAttempt;
import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.domain.Passage;
import com.vocablearn.domain.VocabularyWord;
import com.vocablearn.domain.WordMastery;
import com.vocablearn.dto.ExerciseAnswerDto;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseItemResultDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.exception.ResourceNotFoundException;
import com.vocablearn.exercise.ExerciseGeneratorFactory;
import com.vocablearn.exercise.ExerciseGraderFactory;
import com.vocablearn.repository.AppUserRepository;
import com.vocablearn.repository.ExerciseAttemptRepository;
import com.vocablearn.repository.PassageRepository;
import com.vocablearn.repository.VocabularyWordRepository;
import com.vocablearn.repository.WordMasteryRepository;
import com.vocablearn.service.ExerciseService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseServiceImpl implements ExerciseService {

	private final VocabularyWordRepository vocabularyWordRepository;
	private final PassageRepository passageRepository;
	private final ExerciseGeneratorFactory generatorFactory;
	private final ExerciseGraderFactory graderFactory;
	private final ExerciseAttemptRepository exerciseAttemptRepository;
	private final WordMasteryRepository wordMasteryRepository;
	private final AppUserRepository appUserRepository;

	@Override
	@Transactional(readOnly = true)
	public ExerciseDto generate(Long passageId, ExerciseType type) {
		List<VocabularyWord> words = vocabularyWordRepository.findByPassageIdOrderByOrderIndexAsc(passageId);
		return generatorFactory.get(type).generate(asLearnables(words));
	}

	@Override
	public ExerciseResultDto submit(Long userId, Long passageId, ExerciseType type, ExerciseSubmissionDto submission) {
		List<VocabularyWord> words = vocabularyWordRepository.findByPassageIdOrderByOrderIndexAsc(passageId);
		ExerciseResultDto result = graderFactory.get(type).grade(asLearnables(words), submission);

		Passage passage = passageRepository.findById(passageId)
				.orElseThrow(() -> new ResourceNotFoundException("Passage not found: " + passageId));

		ExerciseAttempt attempt = new ExerciseAttempt();
		attempt.setUser(appUserRepository.getReferenceById(userId));
		attempt.setPassage(passage);
		attempt.setExerciseType(type);
		attempt.setTotalItems(result.totalCount());
		attempt.setCorrectItems(result.correctCount());
		attempt.setAnsweredAt(Instant.now());
		exerciseAttemptRepository.save(attempt);

		updateWordMastery(userId, words, result.itemResults());

		return result;
	}

	private void updateWordMastery(Long userId, List<VocabularyWord> words, List<ExerciseItemResultDto> itemResults) {
		Map<String, VocabularyWord> wordsById = words.stream()
				.collect(Collectors.toMap(w -> w.getId().toString(), Function.identity()));

		for (ExerciseItemResultDto itemResult : itemResults) {
			VocabularyWord word = wordsById.get(itemResult.itemId());
			if (word == null) {
				continue;
			}
			WordMastery mastery = wordMasteryRepository.findByUserIdAndVocabularyWordId(userId, word.getId())
					.orElseGet(() -> {
						WordMastery created = new WordMastery();
						created.setUser(appUserRepository.getReferenceById(userId));
						created.setVocabularyWord(word);
						return created;
					});
			if (itemResult.correct()) {
				mastery.setCorrectCount(mastery.getCorrectCount() + 1);
			} else {
				mastery.setIncorrectCount(mastery.getIncorrectCount() + 1);
			}
			mastery.setLastPracticedAt(Instant.now());
			wordMasteryRepository.save(mastery);
		}
	}

	private List<Learnable> asLearnables(List<VocabularyWord> words) {
		return words.stream().map(Learnable.class::cast).toList();
	}
}
