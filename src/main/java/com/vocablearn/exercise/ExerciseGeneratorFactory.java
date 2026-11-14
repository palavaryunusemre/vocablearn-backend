package com.vocablearn.exercise;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.exception.BadRequestException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Resolves the right {@link ExerciseGenerator} strategy by {@link ExerciseType}.
 * Adding a new exercise type means adding a new {@code @Component} implementing
 * the interface -- this factory needs no changes (Open/Closed).
 */
@Component
public class ExerciseGeneratorFactory {

	private final Map<ExerciseType, ExerciseGenerator> generatorsByType;

	public ExerciseGeneratorFactory(List<ExerciseGenerator> generators) {
		this.generatorsByType = generators.stream()
				.collect(Collectors.toMap(ExerciseGenerator::supports, Function.identity()));
	}

	public ExerciseGenerator get(ExerciseType type) {
		ExerciseGenerator generator = generatorsByType.get(type);
		if (generator == null) {
			throw new BadRequestException("Unsupported exercise type: " + type);
		}
		return generator;
	}
}
