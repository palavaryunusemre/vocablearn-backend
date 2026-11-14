package com.vocablearn.service.impl;

import com.vocablearn.domain.ConjunctionCategory;
import com.vocablearn.dto.ConjunctionCategoryDetailDto;
import com.vocablearn.dto.ConjunctionCategorySummaryDto;
import com.vocablearn.dto.ConjunctionEntryWithCategoryDto;
import com.vocablearn.exception.ResourceNotFoundException;
import com.vocablearn.mapper.ConjunctionMapper;
import com.vocablearn.repository.ConjunctionCategoryRepository;
import com.vocablearn.repository.ConjunctionEntryRepository;
import com.vocablearn.service.ConjunctionCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConjunctionCategoryServiceImpl implements ConjunctionCategoryService {

	private final ConjunctionCategoryRepository categoryRepository;
	private final ConjunctionEntryRepository entryRepository;
	private final ConjunctionMapper conjunctionMapper;

	@Override
	public List<ConjunctionCategorySummaryDto> listCategories() {
		return categoryRepository.findAllByOrderByOrderIndexAsc().stream()
				.map(conjunctionMapper::toSummary)
				.toList();
	}

	@Override
	public ConjunctionCategoryDetailDto getCategoryDetail(Long categoryId) {
		ConjunctionCategory category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Conjunction category not found: " + categoryId));
		return conjunctionMapper.toDetail(category);
	}

	@Override
	public List<ConjunctionEntryWithCategoryDto> listAllEntries() {
		return entryRepository.findAllByOrderByCategory_OrderIndexAscOrderIndexAsc().stream()
				.map(conjunctionMapper::toEntryWithCategoryDto)
				.toList();
	}
}
