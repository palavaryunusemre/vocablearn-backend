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
 * One exam question within a {@link QuizSet}: a sentence with one or two
 * blanks ("----") and 5 fixed options (A-E), exactly as printed on the
 * source exam. Options are plain columns rather than a child entity because
 * the shape is always exactly 5 and never needs independent CRUD.
 */
@Entity
@Table(name = "quiz_questions")
@Getter
@Setter
@NoArgsConstructor
public class QuizQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quiz_set_id", nullable = false)
	private QuizSet quizSet;

	private int orderIndex;

	@Column(name = "exam_label", length = 100)
	private String examLabel;

	@Column(name = "prompt_text", columnDefinition = "TEXT", nullable = false)
	private String promptText;

	@Column(name = "turkish_translation", columnDefinition = "TEXT")
	private String turkishTranslation;

	@Column(name = "option_a", columnDefinition = "TEXT", nullable = false)
	private String optionA;

	@Column(name = "option_b", columnDefinition = "TEXT", nullable = false)
	private String optionB;

	@Column(name = "option_c", columnDefinition = "TEXT", nullable = false)
	private String optionC;

	@Column(name = "option_d", columnDefinition = "TEXT", nullable = false)
	private String optionD;

	@Column(name = "option_e", columnDefinition = "TEXT", nullable = false)
	private String optionE;

	@Column(name = "correct_option", nullable = false, length = 1)
	private String correctOption;

	/**
	 * Server-authored, question-specific "why this is the correct answer"
	 * note (YÖKDİL/YDS-style, terse). Nullable: the original
	 * tense/modals/passive/gerund set has none and instead relies on the
	 * client-side pattern-matching "Konu Tekrarı" panel.
	 */
	@Column(columnDefinition = "TEXT")
	private String explanation;
}
