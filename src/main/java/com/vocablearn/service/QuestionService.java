package com.vocablearn.service;

import com.vocablearn.dto.QuestionSubmissionDto;
import com.vocablearn.dto.QuestionSubmissionResultDto;

public interface QuestionService {

	QuestionSubmissionResultDto submit(Long userId, Long passageId, QuestionSubmissionDto submission);
}
