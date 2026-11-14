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
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
		name = "conjunction_mastery",
		uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "conjunction_entry_id"}))
@Getter
@Setter
@NoArgsConstructor
public class ConjunctionMastery {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private AppUser user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conjunction_entry_id", nullable = false)
	private ConjunctionEntry conjunctionEntry;

	@Column(name = "correct_count", nullable = false)
	private int correctCount;

	@Column(name = "incorrect_count", nullable = false)
	private int incorrectCount;

	@Column(name = "last_practiced_at")
	private Instant lastPracticedAt;
}
