package com.vocablearn.controller;

import com.vocablearn.dto.ModalCategoryDetailDto;
import com.vocablearn.dto.ModalCategorySummaryDto;
import com.vocablearn.service.ModalCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modals")
@RequiredArgsConstructor
public class ModalCategoryController {

	private final ModalCategoryService modalCategoryService;

	@GetMapping
	public List<ModalCategorySummaryDto> listCategories() {
		return modalCategoryService.listCategories();
	}

	@GetMapping("/{id}")
	public ModalCategoryDetailDto getCategory(@PathVariable Long id) {
		return modalCategoryService.getCategoryDetail(id);
	}
}
