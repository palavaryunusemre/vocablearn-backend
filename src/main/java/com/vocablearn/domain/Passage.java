package com.vocablearn.domain;

import jakarta.persistence.CascadeType;
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

@Entity
@Table(name = "passages")
@Getter
@Setter
@NoArgsConstructor
public class Passage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String level;

	private int orderIndex;

	@OneToMany(mappedBy = "passage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("orderIndex asc")
	private List<PassageParagraph> paragraphs = new ArrayList<>();

	@OneToMany(mappedBy = "passage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("orderIndex asc")
	private List<VocabularyWord> vocabulary = new ArrayList<>();

	@OneToMany(mappedBy = "passage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("orderIndex asc")
	private List<ComprehensionQuestion> questions = new ArrayList<>();
}
