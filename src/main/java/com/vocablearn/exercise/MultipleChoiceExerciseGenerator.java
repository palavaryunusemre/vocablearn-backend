package com.vocablearn.exercise;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseItemDto;
import com.vocablearn.dto.ExerciseOptionDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Prompt is the term; the correct option is the item's own meaning,
 * distractors are other items' meanings. optionId == the owning item's id,
 * so grading is a simple id-equality check (see {@link MultipleChoiceExerciseGrader}).
 */
@Component
public class MultipleChoiceExerciseGenerator implements ExerciseGenerator {

	private static final int OPTION_COUNT = 4;

	@Override
	public ExerciseType supports() {
		return ExerciseType.MULTIPLE_CHOICE;
	}

	@Override
	public ExerciseDto generate(List<Learnable> items) {
		List<ExerciseItemDto> result = new ArrayList<>();
		for (Learnable item : ExerciseSupport.shuffledCopy(items)) {
			List<Learnable> distractors = ExerciseSupport.pickDistractors(items, item, OPTION_COUNT - 1);

			List<ExerciseOptionDto> options = new ArrayList<>();
			options.add(new ExerciseOptionDto(item.getLearnableId().toString(), item.getMeaning()));
			for (Learnable distractor : distractors) {
				options.add(new ExerciseOptionDto(distractor.getLearnableId().toString(), distractor.getMeaning()));
			}

			result.add(new ExerciseItemDto(
					item.getLearnableId().toString(), item.getTerm(), ExerciseSupport.shuffledCopy(options)));
		}
		return ExerciseDto.ofItems(supports().name(), result);
	}
}
