package com.vocablearn.controller;

import com.vocablearn.dto.ProgressDto;
import com.vocablearn.security.AppUserPrincipal;
import com.vocablearn.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

	private final ProgressService progressService;

	@GetMapping("/me")
	public ProgressDto getMyProgress(@AuthenticationPrincipal AppUserPrincipal principal) {
		return progressService.getProgress(principal.getId());
	}
}
