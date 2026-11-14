package com.vocablearn.dto;

import java.util.List;

public record ExerciseSubmissionDto(
		List<ExerciseAnswerDto> answers,
		List<MatchAnswerDto> matchAnswers,
		List<TypingAnswerDto> typingAnswers) {
}
