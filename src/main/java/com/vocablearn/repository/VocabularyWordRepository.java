package com.vocablearn.repository;

import com.vocablearn.domain.VocabularyWord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabularyWordRepository extends JpaRepository<VocabularyWord, Long> {

	List<VocabularyWord> findByPassageIdOrderByOrderIndexAsc(Long passageId);

	long countByPassageId(Long passageId);
}
