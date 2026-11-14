package com.vocablearn.repository;

import com.vocablearn.domain.WordMastery;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordMasteryRepository extends JpaRepository<WordMastery, Long> {

	List<WordMastery> findByUserId(Long userId);

	Optional<WordMastery> findByUserIdAndVocabularyWordId(Long userId, Long vocabularyWordId);
}
