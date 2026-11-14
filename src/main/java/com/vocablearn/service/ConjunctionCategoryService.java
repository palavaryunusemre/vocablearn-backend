package com.vocablearn.service;

import com.vocablearn.dto.ConjunctionCategoryDetailDto;
import com.vocablearn.dto.ConjunctionCategorySummaryDto;
import com.vocablearn.dto.ConjunctionEntryWithCategoryDto;
import java.util.List;

public interface ConjunctionCategoryService {

	List<ConjunctionCategorySummaryDto> listCategories();

	ConjunctionCategoryDetailDto getCategoryDetail(Long categoryId);

	/** Every entry across every category, in category/entry order, for the "browse all in sequence" card view. */
	List<ConjunctionEntryWithCategoryDto> listAllEntries();
}
