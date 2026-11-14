package com.vocablearn.exercise;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.dto.ExerciseItemResultDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import com.vocablearn.dto.TypingAnswerDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Unlike the other exercise types, TYPING has no options to compare ids
 * against -- the submitted free text is checked against the actual item
 * list (case-insensitive, trimmed) to see if it matches the primary term.
 * {@code correctOptionId} on the result is repurposed to carry the correct
 * spelling, revealed only after an attempt (same timing as the other games
 * reveal their answer key).
 */
@Component
public class TypingExerciseGrader implements ExerciseGrader {

	@Override
	public ExerciseType supports() {
		return ExerciseType.TYPING;
	}

	@Override
	public ExerciseResultDto grade(List<Learnable> items, ExerciseSubmissionDto submission) {
		Map<String, Learnable> itemsById = items.stream()
				.collect(Collectors.toMap(w -> w.getLearnableId().toString(), Function.identity()));

		List<ExerciseItemResultDto> results = new ArrayList<>();
		int correctCount = 0;
		for (TypingAnswerDto answer : submission.typingAnswers()) {
			Learnable item = itemsById.get(answer.itemId());
			String correctSpelling = item != null ? ExerciseSupport.primaryTerm(item.getTerm()) : "";
			boolean correct = item != null && normalize(answer.typedText()).equals(normalize(correctSpelling));
			if (correct) {
				correctCount++;
			}
			results.add(new ExerciseItemResultDto(answer.itemId(), correct, correctSpelling));
		}

		int total = results.size();
		int percentage = total == 0 ? 0 : (correctCount * 100) / total;
		return new ExerciseResultDto(correctCount, total, percentage, results);
	}

	private String normalize(String text) {
		return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
	}
}
