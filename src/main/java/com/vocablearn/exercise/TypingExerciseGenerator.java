package com.vocablearn.exercise;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseItemDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Prompt is the meaning; the learner types the term from memory instead of
 * recognizing it among options -- active recall ("testing effect") rather
 * than passive recognition. No options are generated since there's nothing
 * to pick from; grading compares free text, see {@link TypingExerciseGrader}.
 */
@Component
public class TypingExerciseGenerator implements ExerciseGenerator {

	@Override
	public ExerciseType supports() {
		return ExerciseType.TYPING;
	}

	@Override
	public ExerciseDto generate(List<Learnable> items) {
		List<ExerciseItemDto> result = new ArrayList<>();
		for (Learnable item : ExerciseSupport.shuffledCopy(items)) {
			result.add(new ExerciseItemDto(item.getLearnableId().toString(), item.getMeaning(), List.of()));
		}
		return ExerciseDto.ofItems(supports().name(), result);
	}
}
