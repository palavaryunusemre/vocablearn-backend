package com.vocablearn.service.impl;

import com.vocablearn.domain.QuizSet;
import com.vocablearn.dto.QuizSetDetailDto;
import com.vocablearn.dto.QuizSetSummaryDto;
import com.vocablearn.exception.ResourceNotFoundException;
import com.vocablearn.mapper.QuizMapper;
import com.vocablearn.repository.QuizSetRepository;
import com.vocablearn.service.QuizSetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizSetServiceImpl implements QuizSetService {

	private final QuizSetRepository quizSetRepository;
	private final QuizMapper quizMapper;

	@Override
	public List<QuizSetSummaryDto> listQuizSets() {
		return quizSetRepository.findAllByOrderByOrderIndexAsc().stream()
				.map(quizMapper::toSummary)
				.toList();
	}

	@Override
	public QuizSetDetailDto getQuizSetDetail(Long quizSetId) {
		QuizSet quizSet = quizSetRepository.findById(quizSetId)
				.orElseThrow(() -> new ResourceNotFoundException("Quiz set not found: " + quizSetId));
		return quizMapper.toDetail(quizSet);
	}
}
