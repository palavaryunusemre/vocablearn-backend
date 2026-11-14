package com.vocablearn.controller;

import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.service.ModalCategoryQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modals/category-quiz")
@RequiredArgsConstructor
public class ModalCategoryQuizController {

	private final ModalCategoryQuizService modalCategoryQuizService;

	@GetMapping
	public ExerciseDto generate(@RequestParam(defaultValue = "10") int count) {
		return modalCategoryQuizService.generate(count);
	}

	@PostMapping("/submit")
	public ExerciseResultDto submit(@RequestBody ExerciseSubmissionDto submission) {
		return modalCategoryQuizService.submit(submission);
	}
}
