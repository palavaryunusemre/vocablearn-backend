package com.vocablearn.exercise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Shared helpers for exercise generators (distractor sampling, shuffling). */
final class ExerciseSupport {

	private ExerciseSupport() {
	}

	static <T> List<T> pickDistractors(List<T> pool, T exclude, int count) {
		List<T> candidates = new ArrayList<>(pool);
		candidates.remove(exclude);
		Collections.shuffle(candidates);
		return candidates.subList(0, Math.min(count, candidates.size()));
	}

	static <T> List<T> shuffledCopy(List<T> list) {
		List<T> copy = new ArrayList<>(list);
		Collections.shuffle(copy);
		return copy;
	}

	/** "intervene in/with" -> "intervene in" -- the ETL keeps alternate forms joined by "/"; only the first is used. */
	static String primaryTerm(String englishTerm) {
		return englishTerm.split("/")[0].trim();
	}
}
