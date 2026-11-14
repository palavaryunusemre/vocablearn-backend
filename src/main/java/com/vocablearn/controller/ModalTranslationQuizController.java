package com.vocablearn.controller;

import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.service.ModalTranslationQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modals/translation-quiz")
@RequiredArgsConstructor
public class ModalTranslationQuizController {

	private final ModalTranslationQuizService modalTranslationQuizService;

	@GetMapping
	public ExerciseDto generate(@RequestParam(defaultValue = "10") int count) {
		return modalTranslationQuizService.generate(count);
	}

	@PostMapping("/submit")
	public ExerciseResultDto submit(@RequestBody ExerciseSubmissionDto submission) {
		return modalTranslationQuizService.submit(submission);
	}
}
