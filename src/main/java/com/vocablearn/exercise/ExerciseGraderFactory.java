package com.vocablearn.exercise;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.exception.BadRequestException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ExerciseGraderFactory {

	private final Map<ExerciseType, ExerciseGrader> gradersByType;

	public ExerciseGraderFactory(List<ExerciseGrader> graders) {
		this.gradersByType = graders.stream()
				.collect(Collectors.toMap(ExerciseGrader::supports, Function.identity()));
	}

	public ExerciseGrader get(ExerciseType type) {
		ExerciseGrader grader = gradersByType.get(type);
		if (grader == null) {
			throw new BadRequestException("Unsupported exercise type: " + type);
		}
		return grader;
	}
}
