package com.vocablearn.service;

import com.vocablearn.dto.QuizSetDetailDto;
import com.vocablearn.dto.QuizSetSummaryDto;
import java.util.List;

public interface QuizSetService {

	List<QuizSetSummaryDto> listQuizSets();

	QuizSetDetailDto getQuizSetDetail(Long quizSetId);
}
