package com.vocablearn.mapper;

import com.vocablearn.domain.ConjunctionCategory;
import com.vocablearn.domain.ConjunctionEntry;
import com.vocablearn.domain.ExerciseType;
import com.vocablearn.dto.ConjunctionCategoryDetailDto;
import com.vocablearn.dto.ConjunctionCategorySummaryDto;
import com.vocablearn.dto.ConjunctionEntryDto;
import com.vocablearn.dto.ConjunctionEntryWithCategoryDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ConjunctionMapper {

	public ConjunctionCategorySummaryDto toSummary(ConjunctionCategory category) {
		return new ConjunctionCategorySummaryDto(
				category.getId(),
				category.getOrderIndex(),
				category.getTitle(),
				category.getUsageNote(),
				category.getEntries().size());
	}

	public ConjunctionCategoryDetailDto toDetail(ConjunctionCategory category) {
		return new ConjunctionCategoryDetailDto(
				category.getId(),
				category.getOrderIndex(),
				category.getTitle(),
				category.getUsageNote(),
				category.getEntries().stream().map(this::toEntryDto).toList(),
				availableExerciseTypes(category.getEntries()));
	}

	public ConjunctionEntryDto toEntryDto(ConjunctionEntry entry) {
		return new ConjunctionEntryDto(
				entry.getId(),
				entry.getOrderIndex(),
				entry.getEnglishTerm(),
				entry.getTurkishMeaning(),
				entry.getExampleSentence());
	}

	public ConjunctionEntryWithCategoryDto toEntryWithCategoryDto(ConjunctionEntry entry) {
		return new ConjunctionEntryWithCategoryDto(
				entry.getId(),
				entry.getEnglishTerm(),
				entry.getTurkishMeaning(),
				entry.getExampleSentence(),
				entry.getCategory().getId(),
				entry.getCategory().getTitle());
	}

	/**
	 * Unlike {@link com.vocablearn.domain.VocabularyWord}, where turkishMeaning
	 * is always a distinct, non-blank translation, a sizeable chunk of the
	 * ADVERBIAL CLAUSES source groups several English synonyms under one
	 * shared Turkish gloss ("because/since/for/as" all -&gt; "-dığı için") and
	 * a few leftover pages never yielded a translation at all. Both make
	 * MULTIPLE_CHOICE/MATCHING degenerate (every option is visually
	 * identical, or blank) without being functionally broken -- so rather
	 * than letting the frontend offer a confusing/empty game, this computes
	 * which exercise types are actually meaningful for a given category's
	 * data shape.
	 */
	private List<ExerciseType> availableExerciseTypes(List<ConjunctionEntry> entries) {
		List<String> meanings = entries.stream()
				.map(ConjunctionEntry::getTurkishMeaning)
				.filter(m -> m != null && !m.isBlank())
				.toList();
		Set<String> distinctMeanings = meanings.stream().collect(Collectors.toSet());
		boolean hasExample = entries.stream()
				.anyMatch(e -> e.getExampleSentence() != null && !e.getExampleSentence().isBlank());

		List<ExerciseType> types = new ArrayList<>();
		if (!meanings.isEmpty()) {
			types.add(ExerciseType.TYPING);
		}
		if (hasExample) {
			types.add(ExerciseType.FILL_BLANK);
		}
		if (distinctMeanings.size() >= 2) {
			types.add(ExerciseType.MULTIPLE_CHOICE);
			types.add(ExerciseType.MATCHING);
		}
		return types;
	}
}
