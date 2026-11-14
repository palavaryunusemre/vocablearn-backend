package com.vocablearn.repository;

import com.vocablearn.domain.Passage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassageRepository extends JpaRepository<Passage, Long> {

	List<Passage> findAllByOrderByOrderIndexAsc();

	List<Passage> findAllByLevelOrderByOrderIndexAsc(String level);

	boolean existsByOrderIndex(int orderIndex);
}
