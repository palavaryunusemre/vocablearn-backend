package com.vocablearn.exercise;

import com.vocablearn.dto.ExerciseAnswerDto;
import com.vocablearn.dto.ExerciseItemResultDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.MatchAnswerDto;
import java.util.ArrayList;
import java.util.List;

/**
 * MULTIPLE_CHOICE, FILL_BLANK and MATCHING all share the same trick: the
 * correct answer for an item/pair is encoded as "selected id equals item id"
 * rather than stored separately, so grading never needs to look up a secret
 * answer key.
 */
final class IdEqualityGrading {

	private IdEqualityGrading() {
	}

	static ExerciseResultDto gradeAnswers(List<ExerciseAnswerDto> answers) {
		List<ExerciseItemResultDto> results = new ArrayList<>();
		int correctCount = 0;
		for (ExerciseAnswerDto answer : answers) {
			boolean correct = answer.itemId().equals(answer.selectedOptionId());
			if (correct) {
				correctCount++;
			}
			results.add(new ExerciseItemResultDto(answer.itemId(), correct, answer.itemId()));
		}
		return toResult(results, correctCount);
	}

	static ExerciseResultDto gradeMatches(List<MatchAnswerDto> matchAnswers) {
		List<ExerciseItemResultDto> results = new ArrayList<>();
		int correctCount = 0;
		for (MatchAnswerDto answer : matchAnswers) {
			boolean correct = answer.leftId().equals(answer.rightId());
			if (correct) {
				correctCount++;
			}
			results.add(new ExerciseItemResultDto(answer.leftId(), correct, answer.leftId()));
		}
		return toResult(results, correctCount);
	}

	private static ExerciseResultDto toResult(List<ExerciseItemResultDto> results, int correctCount) {
		int total = results.size();
		int percentage = total == 0 ? 0 : (correctCount * 100) / total;
		return new ExerciseResultDto(correctCount, total, percentage, results);
	}
}
