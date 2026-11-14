package com.vocablearn.exercise;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.MatchPairItemDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Left items are terms, right items are meanings, both independently
 * shuffled. A left/right pair shares the same id (the item's id) --
 * grading is an id-equality check, see {@link MatchingExerciseGrader}.
 *
 * <p>Returns every item (not just a sample) -- the frontend splits them
 * into manageable rounds for display rather than this generator silently
 * dropping items.
 */
@Component
public class MatchingExerciseGenerator implements ExerciseGenerator {

	@Override
	public ExerciseType supports() {
		return ExerciseType.MATCHING;
	}

	@Override
	public ExerciseDto generate(List<Learnable> items) {
		List<Learnable> selected = ExerciseSupport.shuffledCopy(items);

		List<MatchPairItemDto> left = new ArrayList<>();
		List<MatchPairItemDto> right = new ArrayList<>();
		for (Learnable item : selected) {
			left.add(new MatchPairItemDto(item.getLearnableId().toString(), item.getTerm()));
			right.add(new MatchPairItemDto(item.getLearnableId().toString(), item.getMeaning()));
		}
		Collections.shuffle(left);
		Collections.shuffle(right);

		return ExerciseDto.ofMatchPairs(supports().name(), left, right);
	}
}
