package com.vocablearn.repository;

import com.vocablearn.domain.ConjunctionMastery;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConjunctionMasteryRepository extends JpaRepository<ConjunctionMastery, Long> {

	List<ConjunctionMastery> findByUserId(Long userId);

	Optional<ConjunctionMastery> findByUserIdAndConjunctionEntryId(Long userId, Long conjunctionEntryId);
}
