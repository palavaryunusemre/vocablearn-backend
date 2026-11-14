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
 * A named set of exam-style multiple-choice questions (e.g. "Son 11 Yılın
 * Çıkmış Tense/Modals/Passive/Gerund &amp; Infinitive Soruları"). Deliberately
 * generic/extensible -- the "QUIZ" section is meant to collect many such
 * exam sets over time, each rendered as its own card, so nothing here is
 * tied to this particular PDF's grammar scope.
 */
@Entity
@Table(name = "quiz_sets")
@Getter
@Setter
@NoArgsConstructor
public class QuizSet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "order_index", nullable = false, unique = true)
	private int orderIndex;

	@OneToMany(mappedBy = "quizSet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("orderIndex asc")
	private List<QuizQuestion> questions = new ArrayList<>();
}
