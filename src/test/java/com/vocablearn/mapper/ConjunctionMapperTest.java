package com.vocablearn.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.vocablearn.domain.ConjunctionCategory;
import com.vocablearn.domain.ConjunctionEntry;
import com.vocablearn.domain.ExerciseType;
import com.vocablearn.dto.ConjunctionCategoryDetailDto;
import com.vocablearn.dto.ConjunctionCategorySummaryDto;
import org.junit.jupiter.api.Test;

class ConjunctionMapperTest {

	private final ConjunctionMapper mapper = new ConjunctionMapper();

	private ConjunctionCategory buildCategory() {
		ConjunctionCategory category = new ConjunctionCategory();
		category.setId(1L);
		category.setOrderIndex(3);
		category.setTitle("ZITLIK BAGLACLARI (ama, fakat . Tam Cumle)");
		category.setUsageNote("arkalarindan TAM CUMLE alirlar.");

		ConjunctionEntry entry = new ConjunctionEntry();
		entry.setId(10L);
		entry.setCategory(category);
		entry.setOrderIndex(1);
		entry.setEnglishTerm("although");
		entry.setTurkishMeaning("-e ragmen");
		entry.setExampleSentence("Although it rained, we went out.");
		category.getEntries().add(entry);

		return category;
	}

	private ConjunctionEntry entry(long id, String term, String meaning, String example) {
		ConjunctionEntry entry = new ConjunctionEntry();
		entry.setId(id);
		entry.setEnglishTerm(term);
		entry.setTurkishMeaning(meaning);
		entry.setExampleSentence(example);
		return entry;
	}

	@Test
	void toSummary_mapsBasicFieldsAndEntryCount() {
		ConjunctionCategory category = buildCategory();

		ConjunctionCategorySummaryDto summary = mapper.toSummary(category);

		assertThat(summary.id()).isEqualTo(1L);
		assertThat(summary.order()).isEqualTo(3);
		assertThat(summary.title()).isEqualTo("ZITLIK BAGLACLARI (ama, fakat . Tam Cumle)");
		assertThat(summary.usageNote()).isEqualTo("arkalarindan TAM CUMLE alirlar.");
		assertThat(summary.entryCount()).isEqualTo(1);
	}

	@Test
	void toDetail_mapsEntries() {
		ConjunctionCategory category = buildCategory();

		ConjunctionCategoryDetailDto detail = mapper.toDetail(category);

		assertThat(detail.entries()).hasSize(1);
		assertThat(detail.entries().get(0).englishTerm()).isEqualTo("although");
		assertThat(detail.entries().get(0).turkishMeaning()).isEqualTo("-e ragmen");
		assertThat(detail.entries().get(0).exampleSentence()).isEqualTo("Although it rained, we went out.");
	}

	@Test
	void toEntryWithCategoryDto_mapsEntryAndCategoryFields() {
		ConjunctionCategory category = new ConjunctionCategory();
		category.setId(7L);
		category.setTitle("ZITLIK BAGLACLARI (ama, fakat . Tam Cumle)");

		ConjunctionEntry entry = new ConjunctionEntry();
		entry.setId(5L);
		entry.setCategory(category);
		entry.setEnglishTerm("despite");
		entry.setTurkishMeaning("-e ragmen");
		entry.setExampleSentence("Despite the rain, we went out.");

		var dto = mapper.toEntryWithCategoryDto(entry);

		assertThat(dto.id()).isEqualTo(5L);
		assertThat(dto.englishTerm()).isEqualTo("despite");
		assertThat(dto.turkishMeaning()).isEqualTo("-e ragmen");
		assertThat(dto.exampleSentence()).isEqualTo("Despite the rain, we went out.");
		assertThat(dto.categoryId()).isEqualTo(7L);
		assertThat(dto.categoryTitle()).isEqualTo("ZITLIK BAGLACLARI (ama, fakat . Tam Cumle)");
	}

	@Test
	void toEntryDto_mapsAllFields() {
		ConjunctionEntry entry = new ConjunctionEntry();
		entry.setId(5L);
		entry.setOrderIndex(2);
		entry.setEnglishTerm("despite");
		entry.setTurkishMeaning("-e ragmen");
		entry.setExampleSentence("Despite the rain, we went out.");

		var dto = mapper.toEntryDto(entry);

		assertThat(dto.id()).isEqualTo(5L);
		assertThat(dto.order()).isEqualTo(2);
		assertThat(dto.englishTerm()).isEqualTo("despite");
		assertThat(dto.turkishMeaning()).isEqualTo("-e ragmen");
		assertThat(dto.exampleSentence()).isEqualTo("Despite the rain, we went out.");
	}

	@Test
	void toDetail_singleSharedMeaning_offersOnlyTypingAndFillBlank() {
		// Mirrors the "NEDEN SOYLEYEN BAGLACLAR" pages: several English
		// synonyms (because/since/for/as) all translate to the exact same
		// Turkish gloss. MULTIPLE_CHOICE/MATCHING would show visually
		// identical options, so they shouldn't be offered.
		ConjunctionCategory category = new ConjunctionCategory();
		category.getEntries().add(entry(1L, "because", "-digi icin", "Many records were lost because of war."));
		category.getEntries().add(entry(2L, "since", "-digi icin", "Since it rained, we stayed home."));
		category.getEntries().add(entry(3L, "for", "-digi icin", "The city fell for it was undefended."));

		ConjunctionCategoryDetailDto detail = mapper.toDetail(category);

		assertThat(detail.availableExerciseTypes())
				.containsExactlyInAnyOrder(ExerciseType.TYPING, ExerciseType.FILL_BLANK);
	}

	@Test
	void toDetail_multipleDistinctMeanings_offersAllFourTypes() {
		ConjunctionCategory category = new ConjunctionCategory();
		category.getEntries().add(entry(1L, "if", "eger", "If you try, you will succeed."));
		category.getEntries().add(entry(2L, "unless", "-medikce", "Unless you try, you will fail."));

		ConjunctionCategoryDetailDto detail = mapper.toDetail(category);

		assertThat(detail.availableExerciseTypes()).containsExactlyInAnyOrder(
				ExerciseType.TYPING, ExerciseType.FILL_BLANK, ExerciseType.MULTIPLE_CHOICE, ExerciseType.MATCHING);
	}

	@Test
	void toDetail_blankMeaningsAndNoExamples_offersNothing() {
		// Mirrors the "IKILI BAGLACLAR" leftover pages where the ETL never
		// found a translation or an example sentence for these entries.
		ConjunctionCategory category = new ConjunctionCategory();
		category.getEntries().add(entry(1L, "not only ... but also", "", null));
		category.getEntries().add(entry(2L, "neither ... nor", "", null));

		ConjunctionCategoryDetailDto detail = mapper.toDetail(category);

		assertThat(detail.availableExerciseTypes()).isEmpty();
	}

	@Test
	void toDetail_blankMeaningsButHasExamples_offersOnlyFillBlank() {
		ConjunctionCategory category = new ConjunctionCategory();
		category.getEntries().add(entry(1L, "as if", "", "He acted as if he knew everything."));
		category.getEntries().add(entry(2L, "regardless of", "", null));

		ConjunctionCategoryDetailDto detail = mapper.toDetail(category);

		assertThat(detail.availableExerciseTypes()).containsExactly(ExerciseType.FILL_BLANK);
	}
}
