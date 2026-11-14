package com.vocablearn.repository;

import com.vocablearn.domain.QuizSet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSetRepository extends JpaRepository<QuizSet, Long> {

	List<QuizSet> findAllByOrderByOrderIndexAsc();
}
