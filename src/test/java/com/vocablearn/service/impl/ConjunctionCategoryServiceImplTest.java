package com.vocablearn.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.vocablearn.domain.ConjunctionCategory;
import com.vocablearn.domain.ConjunctionEntry;
import com.vocablearn.dto.ConjunctionCategoryDetailDto;
import com.vocablearn.dto.ConjunctionCategorySummaryDto;
import com.vocablearn.dto.ConjunctionEntryWithCategoryDto;
import com.vocablearn.exception.ResourceNotFoundException;
import com.vocablearn.mapper.ConjunctionMapper;
import com.vocablearn.repository.ConjunctionCategoryRepository;
import com.vocablearn.repository.ConjunctionEntryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConjunctionCategoryServiceImplTest {

	@Mock
	private ConjunctionCategoryRepository categoryRepository;

	@Mock
	private ConjunctionEntryRepository entryRepository;

	@Mock
	private ConjunctionMapper conjunctionMapper;

	@InjectMocks
	private ConjunctionCategoryServiceImpl categoryService;

	@Test
	void listCategories_returnsAllOrderedByIndex() {
		ConjunctionCategory category = new ConjunctionCategory();
		ConjunctionCategorySummaryDto summary = new ConjunctionCategorySummaryDto(1L, 1, "Title", "note", 5);
		when(categoryRepository.findAllByOrderByOrderIndexAsc()).thenReturn(List.of(category));
		when(conjunctionMapper.toSummary(category)).thenReturn(summary);

		List<ConjunctionCategorySummaryDto> result = categoryService.listCategories();

		assertThat(result).containsExactly(summary);
	}

	@Test
	void getCategoryDetail_found_returnsMappedDetail() {
		ConjunctionCategory category = new ConjunctionCategory();
		ConjunctionCategoryDetailDto detail = new ConjunctionCategoryDetailDto(1L, 1, "Title", "note", List.of(), List.of());
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(conjunctionMapper.toDetail(category)).thenReturn(detail);

		ConjunctionCategoryDetailDto result = categoryService.getCategoryDetail(1L);

		assertThat(result).isEqualTo(detail);
	}

	@Test
	void getCategoryDetail_notFound_throwsResourceNotFoundException() {
		when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> categoryService.getCategoryDetail(99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("99");
	}

	@Test
	void listAllEntries_returnsFlattenedMappedEntries() {
		ConjunctionEntry entry = new ConjunctionEntry();
		ConjunctionEntryWithCategoryDto dto =
				new ConjunctionEntryWithCategoryDto(1L, "because", "-dığı için", null, 2L, "NEDEN SÖYLEYEN BAĞLAÇLAR");
		when(entryRepository.findAllByOrderByCategory_OrderIndexAscOrderIndexAsc()).thenReturn(List.of(entry));
		when(conjunctionMapper.toEntryWithCategoryDto(entry)).thenReturn(dto);

		List<ConjunctionEntryWithCategoryDto> result = categoryService.listAllEntries();

		assertThat(result).containsExactly(dto);
	}
}
