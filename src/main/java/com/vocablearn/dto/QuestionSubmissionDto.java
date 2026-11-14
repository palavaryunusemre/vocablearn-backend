package com.vocablearn.dto;

import java.util.List;

public record QuestionSubmissionDto(List<QuestionAnswerDto> answers) {
}
