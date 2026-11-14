package com.vocablearn.service.impl;

import com.vocablearn.domain.ModalCategory;
import com.vocablearn.dto.ModalCategoryDetailDto;
import com.vocablearn.dto.ModalCategorySummaryDto;
import com.vocablearn.exception.ResourceNotFoundException;
import com.vocablearn.mapper.ModalMapper;
import com.vocablearn.repository.ModalCategoryRepository;
import com.vocablearn.service.ModalCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModalCategoryServiceImpl implements ModalCategoryService {

	private final ModalCategoryRepository categoryRepository;
	private final ModalMapper modalMapper;

	@Override
	public List<ModalCategorySummaryDto> listCategories() {
		return categoryRepository.findAllByOrderByOrderIndexAsc().stream()
				.map(modalMapper::toSummary)
				.toList();
	}

	@Override
	public ModalCategoryDetailDto getCategoryDetail(Long categoryId) {
		ModalCategory category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Modal category not found: " + categoryId));
		return modalMapper.toDetail(category);
	}
}
