package com.vocablearn.repository;

import com.vocablearn.domain.QuestionAttempt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt, Long> {

	List<QuestionAttempt> findByUserId(Long userId);

	Optional<QuestionAttempt> findByUserIdAndQuestionId(Long userId, Long questionId);
}
