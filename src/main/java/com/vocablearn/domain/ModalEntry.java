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
 * One modal form (e.g. "could" -&gt; "yapabilirdi (geçmişte genel yetenek)") within a {@link ModalCategory}.
 * Implements {@link Learnable} so the same MULTIPLE_CHOICE/MATCHING exercise Strategy classes written for
 * {@link com.vocablearn.domain.VocabularyWord}/{@code ConjunctionEntry} can quiz on these too (see
 * ModalTranslationQuizService).
 */
@Entity
@Table(name = "modal_entries")
@Getter
@Setter
@NoArgsConstructor
public class ModalEntry implements Learnable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private ModalCategory category;

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
