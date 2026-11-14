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

@Entity
@Table(name = "passage_sentences")
@Getter
@Setter
@NoArgsConstructor
public class PassageSentence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "paragraph_id", nullable = false)
	private PassageParagraph paragraph;

	private int orderIndex;

	@Column(name = "english_text", columnDefinition = "TEXT", nullable = false)
	private String englishText;

	@Column(name = "turkish_text", columnDefinition = "TEXT", nullable = false)
	private String turkishText;
}
