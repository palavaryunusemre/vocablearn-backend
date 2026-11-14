package com.vocablearn.exercise;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseItemDto;
import com.vocablearn.dto.ExerciseOptionDto;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Prompt is the item's example sentence with the term blanked out; options
 * are terms (correct term + distractor terms). Only items with a non-blank
 * example sentence can be used -- the ETL leaves some words without one
 * when the term never matched the source text verbatim.
 */
@Component
public class FillInBlankExerciseGenerator implements ExerciseGenerator {

	private static final int OPTION_COUNT = 4;
	private static final String BLANK = "_____";

	@Override
	public ExerciseType supports() {
		return ExerciseType.FILL_BLANK;
	}

	@Override
	public ExerciseDto generate(List<Learnable> items) {
		List<Learnable> eligible = items.stream()
				.filter(w -> w.getExampleSentence() != null && !w.getExampleSentence().isBlank())
				.toList();

		List<ExerciseItemDto> result = new ArrayList<>();
		for (Learnable item : ExerciseSupport.shuffledCopy(eligible)) {
			String primaryTerm = ExerciseSupport.primaryTerm(item.getTerm());
			String blanked = blankOutTerm(item.getExampleSentence(), primaryTerm);
			if (blanked.equals(item.getExampleSentence())) {
				continue; // term didn't match inside its own example sentence -- skip, can't blank it
			}

			List<Learnable> distractors = ExerciseSupport.pickDistractors(eligible, item, OPTION_COUNT - 1);
			List<ExerciseOptionDto> options = new ArrayList<>();
			options.add(new ExerciseOptionDto(item.getLearnableId().toString(), item.getTerm()));
			for (Learnable distractor : distractors) {
				options.add(new ExerciseOptionDto(distractor.getLearnableId().toString(), distractor.getTerm()));
			}

			result.add(new ExerciseItemDto(
					item.getLearnableId().toString(), blanked, ExerciseSupport.shuffledCopy(options)));
		}
		return ExerciseDto.ofItems(supports().name(), result);
	}

	private String blankOutTerm(String sentence, String term) {
		Pattern pattern = Pattern.compile("\\b" + Pattern.quote(term) + "\\b", Pattern.CASE_INSENSITIVE);
		return pattern.matcher(sentence).replaceFirst(BLANK);
	}
}
