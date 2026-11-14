package com.vocablearn.mapper;

import com.vocablearn.domain.ModalCategory;
import com.vocablearn.domain.ModalEntry;
import com.vocablearn.dto.ModalCategoryDetailDto;
import com.vocablearn.dto.ModalCategorySummaryDto;
import com.vocablearn.dto.ModalEntryDto;
import org.springframework.stereotype.Component;

@Component
public class ModalMapper {

	public ModalCategorySummaryDto toSummary(ModalCategory category) {
		return new ModalCategorySummaryDto(
				category.getId(),
				category.getOrderIndex(),
				category.getTitle(),
				category.getUsageNote(),
				category.getEntries().size());
	}

	public ModalCategoryDetailDto toDetail(ModalCategory category) {
		return new ModalCategoryDetailDto(
				category.getId(),
				category.getOrderIndex(),
				category.getTitle(),
				category.getUsageNote(),
				category.getEntries().stream().map(this::toEntryDto).toList());
	}

	public ModalEntryDto toEntryDto(ModalEntry entry) {
		return new ModalEntryDto(
				entry.getId(),
				entry.getOrderIndex(),
				entry.getEnglishTerm(),
				entry.getTurkishMeaning(),
				entry.getExampleSentence());
	}
}
