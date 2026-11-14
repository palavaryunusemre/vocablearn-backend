package com.vocablearn.controller;

import com.vocablearn.dto.ConjunctionCategoryDetailDto;
import com.vocablearn.dto.ConjunctionCategorySummaryDto;
import com.vocablearn.dto.ConjunctionEntryWithCategoryDto;
import com.vocablearn.service.ConjunctionCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conjunctions")
@RequiredArgsConstructor
public class ConjunctionCategoryController {

	private final ConjunctionCategoryService conjunctionCategoryService;

	@GetMapping
	public List<ConjunctionCategorySummaryDto> listCategories() {
		return conjunctionCategoryService.listCategories();
	}

	/** Flattened, cross-category entry list for the "browse all in sequence" card view. */
	@GetMapping("/entries")
	public List<ConjunctionEntryWithCategoryDto> listAllEntries() {
		return conjunctionCategoryService.listAllEntries();
	}

	@GetMapping("/{id}")
	public ConjunctionCategoryDetailDto getCategory(@PathVariable Long id) {
		return conjunctionCategoryService.getCategoryDetail(id);
	}
}
