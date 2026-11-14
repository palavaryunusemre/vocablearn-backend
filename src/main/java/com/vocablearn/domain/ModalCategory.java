package com.vocablearn.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A grouping of related modal forms from the MODALS grammar PDF (e.g.
 * "ABILITY (Yetenek)" -- can/could/be able to/managed to) -- the
 * grammar-section analogue of {@link ConjunctionCategory} for the modals
 * module.
 */
@Entity
@Table(name = "modal_categories")
@Getter
@Setter
@NoArgsConstructor
public class ModalCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(name = "usage_note", columnDefinition = "TEXT")
	private String usageNote;

	@Column(name = "order_index", nullable = false, unique = true)
	private int orderIndex;

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("orderIndex asc")
	private List<ModalEntry> entries = new ArrayList<>();
}
