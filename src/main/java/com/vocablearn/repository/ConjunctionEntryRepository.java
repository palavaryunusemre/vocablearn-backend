package com.vocablearn.repository;

import com.vocablearn.domain.ConjunctionEntry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConjunctionEntryRepository extends JpaRepository<ConjunctionEntry, Long> {

	List<ConjunctionEntry> findByCategoryIdOrderByOrderIndexAsc(Long categoryId);

	long countByCategoryId(Long categoryId);

	List<ConjunctionEntry> findAllByOrderByCategory_OrderIndexAscOrderIndexAsc();
}
