package com.vocablearn.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import com.vocablearn.domain.ConjunctionCategory;
import com.vocablearn.domain.ConjunctionEntry;
import com.vocablearn.dto.ExerciseAnswerDto;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseItemDto;
import com.vocablearn.dto.ExerciseOptionDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import java.util.List;
import org.junit.jupiter.api.Test;

class CategoryQuizGeneratorTest {

	private final CategoryQuizGenerator generator = new CategoryQuizGenerator();

	private ConjunctionEntry entry(long id, String term, String categoryTitle) {
		ConjunctionCategory category = new ConjunctionCategory();
		category.setTitle(categoryTitle);
		ConjunctionEntry entry = new ConjunctionEntry();
		entry.setId(id);
		entry.setEnglishTerm(term);
		entry.setCategory(category);
		return entry;
	}

	/** 3 groups of 4 entries each -- every group is big enough for "which is NOT a member" questions too. */
	private List<ConjunctionEntry> fixtureEntries() {
		return List.of(
				entry(1, "because", "SEBEP BAĞLAÇLARI (çünkü · Tam Cümle)"),
				entry(2, "since", "SEBEP BAĞLAÇLARI (çünkü · Tam Cümle)"),
				entry(3, "as", "SEBEP BAĞLAÇLARI (-dığı için · Tam Cümle)"),
				entry(4, "for", "SEBEP BAĞLAÇLARI (-dığı için · Tam Cümle)"),
				entry(5, "although", "ZITLIK BAĞLAÇLARI (-e rağmen · Tam Cümle)"),
				entry(6, "despite", "ZITLIK BAĞLAÇLARI (-e rağmen · İsim)"),
				entry(7, "but", "ZITLIK BAĞLAÇLARI (ama, fakat · Tam Cümle)"),
				entry(8, "whereas", "ZITLIK BAĞLAÇLARI (ama, fakat · Tam Cümle)"),
				entry(9, "if", "KOŞUL BAĞLAÇLARI"),
				entry(10, "unless", "KOŞUL BAĞLAÇLARI"),
				entry(11, "provided that", "KOŞUL BAĞLAÇLARI"),
				entry(12, "as long as", "KOŞUL BAĞLAÇLARI"));
	}

	@Test
	void groupName_stripsParentheticalSuffix() {
		ConjunctionEntry entry = entry(1, "because", "SEBEP BAĞLAÇLARI (çünkü · Tam Cümle)");
		assertThat(CategoryQuizGenerator.groupName(entry)).isEqualTo("SEBEP BAĞLAÇLARI");
	}

	@Test
	void groupName_subCategoriesShareTheSameGroup() {
		ConjunctionEntry a = entry(5, "although", "ZITLIK BAĞLAÇLARI (-e rağmen · Tam Cümle)");
		ConjunctionEntry b = entry(6, "despite", "ZITLIK BAĞLAÇLARI (-e rağmen · İsim)");
		assertThat(CategoryQuizGenerator.groupName(a)).isEqualTo(CategoryQuizGenerator.groupName(b));
	}

	@Test
	void groupName_noParens_usesFullTitle() {
		ConjunctionEntry entry = entry(9, "if", "KOŞUL BAĞLAÇLARI");
		assertThat(CategoryQuizGenerator.groupName(entry)).isEqualTo("KOŞUL BAĞLAÇLARI");
	}

	@Test
	void generate_producesRequestedQuestionCount_whenEnoughEntriesExist() {
		ExerciseDto exercise = generator.generate(fixtureEntries(), 10);

		assertThat(exercise.exerciseType()).isEqualTo("CATEGORY_QUIZ");
		assertThat(exercise.items()).hasSize(10);
	}

	@Test
	void generate_everyQuestion_hasExactlyFourOptionsWithOneMatchingItemId() {
		ExerciseDto exercise = generator.generate(fixtureEntries(), 12);

		for (ExerciseItemDto item : exercise.items()) {
			assertThat(item.options()).hasSize(4);
			assertThat(item.options()).extracting(ExerciseOptionDto::optionId).doesNotHaveDuplicates();

			long matchingOptions = item.options().stream()
					.filter(o -> o.optionId().equals(item.itemId()))
					.count();
			assertThat(matchingOptions).isEqualTo(1);
		}
	}

	@Test
	void generate_questionText_mentionsTheAskedGroup() {
		ExerciseDto exercise = generator.generate(fixtureEntries(), 10);

		assertThat(exercise.items()).allSatisfy(item -> assertThat(item.prompt())
				.matches(".*(SEBEP BAĞLAÇLARI|ZITLIK BAĞLAÇLARI|KOŞUL BAĞLAÇLARI).*"));
	}

	@Test
	void generate_itemIdsAreUniqueAcrossTheWholeRound() {
		// The frontend keys its answer map by itemId (the correct entry's id);
		// if the same entry were picked as the correct answer for two
		// different questions, both would collapse onto the same answer key.
		// This must hold regardless of how the random target groups happen
		// to be chosen, not just "on average".
		List<ConjunctionEntry> entries = fixtureEntries();
		ExerciseDto exercise = generator.generate(entries, entries.size());

		List<String> itemIds = exercise.items().stream().map(ExerciseItemDto::itemId).toList();
		assertThat(itemIds).doesNotHaveDuplicates();
		assertThat(itemIds).hasSize(entries.size());
	}

	@Test
	void generate_requestingMoreQuestionsThanEntries_degradesGracefullyWithoutDuplicates() {
		List<ConjunctionEntry> entries = fixtureEntries();
		ExerciseDto exercise = generator.generate(entries, entries.size() + 10);

		List<String> itemIds = exercise.items().stream().map(ExerciseItemDto::itemId).toList();
		assertThat(itemIds).doesNotHaveDuplicates();
		assertThat(exercise.items()).hasSizeLessThanOrEqualTo(entries.size());
	}

	@Test
	void generate_withNoEntries_producesNoQuestions() {
		ExerciseDto exercise = generator.generate(List.of(), 10);

		assertThat(exercise.items()).isEmpty();
	}

	@Test
	void grade_matchingItemIdAndSelection_isCorrect() {
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(
				List.of(new ExerciseAnswerDto("3", "3")), List.of(), List.of());

		ExerciseResultDto result = generator.grade(submission);

		assertThat(result.correctCount()).isEqualTo(1);
		assertThat(result.itemResults().get(0).correct()).isTrue();
	}

	@Test
	void grade_mismatchedSelection_isIncorrect() {
		ExerciseSubmissionDto submission = new ExerciseSubmissionDto(
				List.of(new ExerciseAnswerDto("3", "5")), List.of(), List.of());

		ExerciseResultDto result = generator.grade(submission);

		assertThat(result.correctCount()).isEqualTo(0);
		assertThat(result.itemResults().get(0).correct()).isFalse();
	}
}
