package com.vocablearn.mapper;

import com.vocablearn.domain.QuizQuestion;
import com.vocablearn.domain.QuizSet;
import com.vocablearn.dto.QuizOptionDto;
import com.vocablearn.dto.QuizQuestionDto;
import com.vocablearn.dto.QuizSetDetailDto;
import com.vocablearn.dto.QuizSetSummaryDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class QuizMapper {

	public QuizSetSummaryDto toSummary(QuizSet quizSet) {
		return new QuizSetSummaryDto(
				quizSet.getId(),
				quizSet.getOrderIndex(),
				quizSet.getTitle(),
				quizSet.getDescription(),
				quizSet.getQuestions().size());
	}

	public QuizSetDetailDto toDetail(QuizSet quizSet) {
		return new QuizSetDetailDto(
				quizSet.getId(),
				quizSet.getOrderIndex(),
				quizSet.getTitle(),
				quizSet.getDescription(),
				quizSet.getQuestions().stream().map(this::toQuestionDto).toList());
	}

	public QuizQuestionDto toQuestionDto(QuizQuestion question) {
		List<QuizOptionDto> options = List.of(
				new QuizOptionDto("A", question.getOptionA()),
				new QuizOptionDto("B", question.getOptionB()),
				new QuizOptionDto("C", question.getOptionC()),
				new QuizOptionDto("D", question.getOptionD()),
				new QuizOptionDto("E", question.getOptionE()));
		return new QuizQuestionDto(
				question.getId(),
				question.getOrderIndex(),
				question.getExamLabel(),
				question.getPromptText(),
				question.getTurkishTranslation(),
				options,
				question.getCorrectOption(),
				question.getExplanation());
	}
}
