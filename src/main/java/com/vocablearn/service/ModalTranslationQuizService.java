package com.vocablearn.service;

import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;

/**
 * General (cross-category) "what does this modal mean in Turkish?" quiz,
 * sampled from every modal category rather than a single one -- the "genel
 * test" / İngilizce-Türkçe çeviri testi counterpart of the per-category
 * MULTIPLE_CHOICE game.
 */
public interface ModalTranslationQuizService {

	ExerciseDto generate(int questionCount);

	ExerciseResultDto submit(ExerciseSubmissionDto submission);
}
