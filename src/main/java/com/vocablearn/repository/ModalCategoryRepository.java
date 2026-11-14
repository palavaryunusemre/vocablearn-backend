package com.vocablearn.repository;

import com.vocablearn.domain.ModalCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModalCategoryRepository extends JpaRepository<ModalCategory, Long> {

	List<ModalCategory> findAllByOrderByOrderIndexAsc();
}
