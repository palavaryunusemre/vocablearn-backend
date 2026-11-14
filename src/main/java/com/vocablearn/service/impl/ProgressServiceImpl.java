package com.vocablearn.service.impl;

import com.vocablearn.domain.Passage;
import com.vocablearn.domain.QuestionAttempt;
import com.vocablearn.domain.WordMastery;
import com.vocablearn.dto.PassageProgressDto;
import com.vocablearn.dto.ProgressDto;
import com.vocablearn.repository.PassageRepository;
import com.vocablearn.repository.QuestionAttemptRepository;
import com.vocablearn.repository.VocabularyWordRepository;
import com.vocablearn.repository.WordMasteryRepository;
import com.vocablearn.service.ProgressService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressServiceImpl implements ProgressService {

	/** A word counts as "mastered" once answered correctly this many times. */
	private static final int MASTERY_THRESHOLD = 3;

	private final PassageRepository passageRepository;
	private final VocabularyWordRepository vocabularyWordRepository;
	private final WordMasteryRepository wordMasteryRepository;
	private final QuestionAttemptRepository questionAttemptRepository;

	@Override
	public ProgressDto getProgress(Long userId) {
		List<Passage> passages = passageRepository.findAllByOrderByOrderIndexAsc();
		List<WordMastery> masteries = wordMasteryRepository.findByUserId(userId);
		List<QuestionAttempt> attempts = questionAttemptRepository.findByUserId(userId);

		Map<Long, Long> masteredWordsByPassage = masteries.stream()
				.filter(m -> m.getCorrectCount() >= MASTERY_THRESHOLD)
				.collect(Collectors.groupingBy(
						m -> m.getVocabularyWord().getPassage().getId(), Collectors.counting()));

		Map<Long, List<QuestionAttempt>> attemptsByPassage = attempts.stream()
				.collect(Collectors.groupingBy(a -> a.getQuestion().getPassage().getId()));

		List<PassageProgressDto> passageProgress = passages.stream()
				.map(passage -> {
					long totalWords = vocabularyWordRepository.countByPassageId(passage.getId());
					long masteredWords = masteredWordsByPassage.getOrDefault(passage.getId(), 0L);
					List<QuestionAttempt> passageAttempts =
							attemptsByPassage.getOrDefault(passage.getId(), List.of());
					long questionsCorrect = passageAttempts.stream().filter(QuestionAttempt::isCorrect).count();
					return new PassageProgressDto(
							passage.getId(),
							passage.getTitle(),
							(int) totalWords,
							(int) masteredWords,
							passageAttempts.size(),
							(int) questionsCorrect);
				})
				.toList();

		int totalWords = passageProgress.stream().mapToInt(PassageProgressDto::totalWords).sum();
		int masteredWords = passageProgress.stream().mapToInt(PassageProgressDto::masteredWords).sum();
		int questionsAnswered = attempts.size();
		int questionsCorrect = (int) attempts.stream().filter(QuestionAttempt::isCorrect).count();

		return new ProgressDto(totalWords, masteredWords, questionsAnswered, questionsCorrect, passageProgress);
	}
}
