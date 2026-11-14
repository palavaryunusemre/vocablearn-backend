package com.vocablearn.controller;

import com.vocablearn.dto.QuizSetDetailDto;
import com.vocablearn.dto.QuizSetSummaryDto;
import com.vocablearn.service.QuizSetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizSetController {

	private final QuizSetService quizSetService;

	@GetMapping
	public List<QuizSetSummaryDto> listQuizSets() {
		return quizSetService.listQuizSets();
	}

	@GetMapping("/{id}")
	public QuizSetDetailDto getQuizSet(@PathVariable Long id) {
		return quizSetService.getQuizSetDetail(id);
	}
}
