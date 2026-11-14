package com.vocablearn.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vocablearn.domain.ComprehensionQuestion;
import com.vocablearn.domain.Passage;
import com.vocablearn.domain.QuestionAttempt;
import com.vocablearn.dto.QuestionAnswerDto;
import com.vocablearn.dto.QuestionSubmissionDto;
import com.vocablearn.dto.QuestionSubmissionResultDto;
import com.vocablearn.exception.BadRequestException;
import com.vocablearn.exception.ResourceNotFoundException;
import com.vocablearn.repository.AppUserRepository;
import com.vocablearn.repository.ComprehensionQuestionRepository;
import com.vocablearn.repository.QuestionAttemptRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

	@Mock
	private ComprehensionQuestionRepository comprehensionQuestionRepository;

	@Mock
	private QuestionAttemptRepository questionAttemptRepository;

	@Mock
	private AppUserRepository appUserRepository;

	@InjectMocks
	private QuestionServiceImpl questionService;

	private ComprehensionQuestion question(long id, long passageId, int correctOptionIndex) {
		Passage passage = new Passage();
		passage.setId(passageId);
		ComprehensionQuestion question = new ComprehensionQuestion();
		question.setId(id);
		question.setPassage(passage);
		question.setCorrectOptionIndex(correctOptionIndex);
		return question;
	}

	@Test
	void submit_correctAnswer_recordsCorrectAttemptAndResult() {
		ComprehensionQuestion question = question(10L, 5L, 2);
		when(comprehensionQuestionRepository.findById(10L)).thenReturn(Optional.of(question));
		when(questionAttemptRepository.findByUserIdAndQuestionId(7L, 10L)).thenReturn(Optional.empty());

		QuestionSubmissionDto submission = new QuestionSubmissionDto(List.of(new QuestionAnswerDto(10L, 2)));

		QuestionSubmissionResultDto result = questionService.submit(7L, 5L, submission);

		assertThat(result.correctCount()).isEqualTo(1);
		assertThat(result.totalCount()).isEqualTo(1);
		assertThat(result.results().get(0).correct()).isTrue();
		assertThat(result.results().get(0).correctOptionIndex()).isEqualTo(2);

		org.mockito.ArgumentCaptor<QuestionAttempt> captor = org.mockito.ArgumentCaptor.forClass(QuestionAttempt.class);
		verify(questionAttemptRepository).save(captor.capture());
		assertThat(captor.getValue().isCorrect()).isTrue();
		assertThat(captor.getValue().getSelectedOptionIndex()).isEqualTo(2);
	}

	@Test
	void submit_incorrectAnswer_reflectsInResultAndAttempt() {
		ComprehensionQuestion question = question(10L, 5L, 2);
		when(comprehensionQuestionRepository.findById(10L)).thenReturn(Optional.of(question));
		when(questionAttemptRepository.findByUserIdAndQuestionId(7L, 10L)).thenReturn(Optional.empty());

		QuestionSubmissionDto submission = new QuestionSubmissionDto(List.of(new QuestionAnswerDto(10L, 0)));

		QuestionSubmissionResultDto result = questionService.submit(7L, 5L, submission);

		assertThat(result.correctCount()).isEqualTo(0);
		assertThat(result.results().get(0).correct()).isFalse();
	}

	@Test
	void submit_reusesExistingAttemptInsteadOfCreatingDuplicate() {
		ComprehensionQuestion question = question(10L, 5L, 1);
		QuestionAttempt existingAttempt = new QuestionAttempt();
		when(comprehensionQuestionRepository.findById(10L)).thenReturn(Optional.of(question));
		when(questionAttemptRepository.findByUserIdAndQuestionId(7L, 10L)).thenReturn(Optional.of(existingAttempt));

		QuestionSubmissionDto submission = new QuestionSubmissionDto(List.of(new QuestionAnswerDto(10L, 1)));

		questionService.submit(7L, 5L, submission);

		verify(questionAttemptRepository).save(existingAttempt);
		assertThat(existingAttempt.isCorrect()).isTrue();
	}

	@Test
	void submit_questionFromDifferentPassage_throwsBadRequest() {
		ComprehensionQuestion question = question(10L, 999L, 1);
		when(comprehensionQuestionRepository.findById(10L)).thenReturn(Optional.of(question));

		QuestionSubmissionDto submission = new QuestionSubmissionDto(List.of(new QuestionAnswerDto(10L, 1)));

		assertThatThrownBy(() -> questionService.submit(7L, 5L, submission))
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	void submit_unknownQuestion_throwsResourceNotFound() {
		when(comprehensionQuestionRepository.findById(404L)).thenReturn(Optional.empty());

		QuestionSubmissionDto submission = new QuestionSubmissionDto(List.of(new QuestionAnswerDto(404L, 0)));

		assertThatThrownBy(() -> questionService.submit(7L, 5L, submission))
				.isInstanceOf(ResourceNotFoundException.class);
	}
}
