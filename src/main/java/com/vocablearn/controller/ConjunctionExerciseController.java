package com.vocablearn.controller;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.security.AppUserPrincipal;
import com.vocablearn.service.ConjunctionExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conjunctions/{categoryId}/exercises")
@RequiredArgsConstructor
public class ConjunctionExerciseController {

	private final ConjunctionExerciseService conjunctionExerciseService;

	@GetMapping("/{type}")
	public ExerciseDto generate(@PathVariable Long categoryId, @PathVariable ExerciseType type) {
		return conjunctionExerciseService.generate(categoryId, type);
	}

	@PostMapping("/{type}/submit")
	public ExerciseResultDto submit(
			@PathVariable Long categoryId,
			@PathVariable ExerciseType type,
			@RequestBody ExerciseSubmissionDto submission,
			@AuthenticationPrincipal AppUserPrincipal principal) {
		return conjunctionExerciseService.submit(principal.getId(), categoryId, type, submission);
	}
}
