package com.vocablearn.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exercise_attempts")
@Getter
@Setter
@NoArgsConstructor
public class ExerciseAttempt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private AppUser user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "passage_id", nullable = false)
	private Passage passage;

	@Enumerated(EnumType.STRING)
	@Column(name = "exercise_type", nullable = false)
	private ExerciseType exerciseType;

	@Column(name = "total_items", nullable = false)
	private int totalItems;

	@Column(name = "correct_items", nullable = false)
	private int correctItems;

	@Column(name = "answered_at", nullable = false)
	private Instant answeredAt = Instant.now();
}
