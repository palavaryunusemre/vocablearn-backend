package com.vocablearn.service.impl;

import com.vocablearn.domain.ComprehensionQuestion;
import com.vocablearn.domain.QuestionAttempt;
import com.vocablearn.dto.QuestionAnswerDto;
import com.vocablearn.dto.QuestionResultDto;
import com.vocablearn.dto.QuestionSubmissionDto;
import com.vocablearn.dto.QuestionSubmissionResultDto;
import com.vocablearn.exception.BadRequestException;
import com.vocablearn.exception.ResourceNotFoundException;
import com.vocablearn.repository.AppUserRepository;
import com.vocablearn.repository.ComprehensionQuestionRepository;
import com.vocablearn.repository.QuestionAttemptRepository;
import com.vocablearn.service.QuestionService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {

	private final ComprehensionQuestionRepository comprehensionQuestionRepository;
	private final QuestionAttemptRepository questionAttemptRepository;
	private final AppUserRepository appUserRepository;

	@Override
	public QuestionSubmissionResultDto submit(Long userId, Long passageId, QuestionSubmissionDto submission) {
		List<QuestionResultDto> results = new ArrayList<>();
		int correctCount = 0;

		for (QuestionAnswerDto answer : submission.answers()) {
			ComprehensionQuestion question = comprehensionQuestionRepository.findById(answer.questionId())
					.orElseThrow(() -> new ResourceNotFoundException("Question not found: " + answer.questionId()));
			if (!question.getPassage().getId().equals(passageId)) {
				throw new BadRequestException("Question " + answer.questionId() + " does not belong to passage " + passageId);
			}

			boolean correct = question.getCorrectOptionIndex() == answer.selectedOptionIndex();
			if (correct) {
				correctCount++;
			}

			QuestionAttempt attempt = questionAttemptRepository
					.findByUserIdAndQuestionId(userId, answer.questionId())
					.orElseGet(() -> {
						QuestionAttempt created = new QuestionAttempt();
						created.setUser(appUserRepository.getReferenceById(userId));
						created.setQuestion(question);
						return created;
					});
			attempt.setSelectedOptionIndex(answer.selectedOptionIndex());
			attempt.setCorrect(correct);
			attempt.setAnsweredAt(Instant.now());
			questionAttemptRepository.save(attempt);

			results.add(new QuestionResultDto(
					answer.questionId(), correct, answer.selectedOptionIndex(), question.getCorrectOptionIndex()));
		}

		return new QuestionSubmissionResultDto(results, correctCount, results.size());
	}
}
