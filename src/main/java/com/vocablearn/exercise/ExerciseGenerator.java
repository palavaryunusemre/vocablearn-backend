package com.vocablearn.exercise;

import com.vocablearn.domain.ExerciseType;
import com.vocablearn.domain.Learnable;
import com.vocablearn.dto.ExerciseDto;
import java.util.List;

/** Strategy interface: one implementation per {@link ExerciseType}, selected by {@link ExerciseGeneratorFactory}. */
public interface ExerciseGenerator {

	ExerciseType supports();

	ExerciseDto generate(List<Learnable> items);
}
