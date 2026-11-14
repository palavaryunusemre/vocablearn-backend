package com.vocablearn.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comprehension_questions")
@Getter
@Setter
@NoArgsConstructor
public class ComprehensionQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "passage_id", nullable = false)
	private Passage passage;

	private int orderIndex;

	@Enumerated(EnumType.STRING)
	@Column(name = "question_type", nullable = false)
	private QuestionType questionType;

	@Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
	private String questionText;

	@ElementCollection
	@CollectionTable(name = "comprehension_question_options", joinColumns = @JoinColumn(name = "question_id"))
	@OrderColumn(name = "option_index")
	@Column(name = "option_text", columnDefinition = "TEXT")
	private List<String> options = new ArrayList<>();

	@Column(name = "correct_option_index", nullable = false)
	private int correctOptionIndex;
}
