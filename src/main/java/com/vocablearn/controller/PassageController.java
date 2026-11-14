package com.vocablearn.controller;

import com.vocablearn.dto.PassageDetailDto;
import com.vocablearn.dto.PassageSummaryDto;
import com.vocablearn.service.PassageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/passages")
@RequiredArgsConstructor
public class PassageController {

	private final PassageService passageService;

	@GetMapping
	public List<PassageSummaryDto> listPassages(@RequestParam(required = false) String level) {
		return passageService.listPassages(level);
	}

	@GetMapping("/{id}")
	public PassageDetailDto getPassage(@PathVariable Long id) {
		return passageService.getPassageDetail(id);
	}
}
