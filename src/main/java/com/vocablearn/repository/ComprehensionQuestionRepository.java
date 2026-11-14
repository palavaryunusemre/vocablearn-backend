package com.vocablearn.repository;

import com.vocablearn.domain.ComprehensionQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComprehensionQuestionRepository extends JpaRepository<ComprehensionQuestion, Long> {

	List<ComprehensionQuestion> findByPassageIdOrderByOrderIndexAsc(Long passageId);
}
