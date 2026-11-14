package com.vocablearn.controller;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.security.AppUserPrincipal;
import com.vocablearn.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/passages/{passageId}/exercises")
@RequiredArgsConstructor
public class ExerciseController {

	private final ExerciseService exerciseService;

	@GetMapping("/{type}")
	public ExerciseDto generate(@PathVariable Long passageId, @PathVariable ExerciseType type) {
		return exerciseService.generate(passageId, type);
	}

	@PostMapping("/{type}/submit")
	public ExerciseResultDto submit(
			@PathVariable Long passageId,
			@PathVariable ExerciseType type,
			@RequestBody ExerciseSubmissionDto submission,
			@AuthenticationPrincipal AppUserPrincipal principal) {
		return exerciseService.submit(principal.getId(), passageId, type, submission);
	}
}
