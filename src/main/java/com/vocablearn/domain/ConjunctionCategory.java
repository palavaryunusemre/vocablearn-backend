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
 * A grouping of related connectors/conjunctions from the ADVERBIAL CLAUSES
 * grammar PDF (e.g. "NEDEN SOYLEYEN BAGLACLAR (-digi icin, cunku - Tam
 * Cumle)") -- the grammar-section analogue of {@link Passage}: a Passage
 * groups VocabularyWords, a ConjunctionCategory groups ConjunctionEntries.
 */
@Entity
@Table(name = "conjunction_categories")
@Getter
@Setter
@NoArgsConstructor
public class ConjunctionCategory {

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
	private List<ConjunctionEntry> entries = new ArrayList<>();
}
