package com.vocablearn.service;

import com.vocablearn.dto.ModalCategoryDetailDto;
import com.vocablearn.dto.ModalCategorySummaryDto;
import java.util.List;

public interface ModalCategoryService {

	List<ModalCategorySummaryDto> listCategories();

	ModalCategoryDetailDto getCategoryDetail(Long categoryId);
}
