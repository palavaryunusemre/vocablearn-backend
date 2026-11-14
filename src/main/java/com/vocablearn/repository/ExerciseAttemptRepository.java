package com.vocablearn.repository;

import com.vocablearn.domain.ExerciseAttempt;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseAttemptRepository extends JpaRepository<ExerciseAttempt, Long> {

	List<ExerciseAttempt> findByUserId(Long userId);

	List<ExerciseAttempt> findByUserIdAndPassageId(Long userId, Long passageId);
}
