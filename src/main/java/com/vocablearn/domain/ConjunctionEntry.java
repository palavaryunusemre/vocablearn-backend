package com.vocablearn.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One connector/conjunction (e.g. "although" -&gt; "-e ragmen") within a
 * {@link ConjunctionCategory}. Implements {@link Learnable} so the exact
 * same exercise Strategy classes written for {@link VocabularyWord} can
 * quiz on these without any duplicated game logic.
 */
@Entity
@Table(name = "conjunction_entries")
@Getter
@Setter
@NoArgsConstructor
public class ConjunctionEntry implements Learnable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private ConjunctionCategory category;

	private int orderIndex;

	@Column(name = "english_term", nullable = false)
	private String englishTerm;

	@Column(name = "turkish_meaning", nullable = false)
	private String turkishMeaning;

	@Column(name = "example_sentence", columnDefinition = "TEXT")
	private String exampleSentence;

	@Override
	public Long getLearnableId() {
		return id;
	}

	@Override
	public String getTerm() {
		return englishTerm;
	}

	@Override
	public String getMeaning() {
		return turkishMeaning;
	}
}
