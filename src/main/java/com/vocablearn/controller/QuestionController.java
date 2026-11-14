package com.vocablearn.controller;

import com.vocablearn.dto.QuestionSubmissionDto;
import com.vocablearn.dto.QuestionSubmissionResultDto;
import com.vocablearn.security.AppUserPrincipal;
import com.vocablearn.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/passages/{passageId}/questions")
@RequiredArgsConstructor
public class QuestionController {

	private final QuestionService questionService;

	@PostMapping("/submit")
	public QuestionSubmissionResultDto submit(
			@PathVariable Long passageId,
			@RequestBody QuestionSubmissionDto submission,
			@AuthenticationPrincipal AppUserPrincipal principal) {
		return questionService.submit(principal.getId(), passageId, submission);
	}
}
