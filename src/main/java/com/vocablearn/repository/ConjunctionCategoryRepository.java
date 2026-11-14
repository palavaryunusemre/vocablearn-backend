package com.vocablearn.repository;

import com.vocablearn.domain.ConjunctionCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConjunctionCategoryRepository extends JpaRepository<ConjunctionCategory, Long> {

	List<ConjunctionCategory> findAllByOrderByOrderIndexAsc();

	boolean existsByOrderIndex(int orderIndex);
}
