package com.vocablearn.domain;

/**
 * Shared shape for anything the exercise Strategy classes (MultipleChoice,
 * FillInBlank/Flashcard, Matching, Typing generators/graders) can quiz on --
 * currently {@link VocabularyWord} (passage vocabulary) and
 * {@code ConjunctionEntry} (grammar connectors). Letting the exercise
 * package depend on this instead of a concrete entity is what lets the same
 * four games be reused for an entirely different content domain without
 * duplicating any generator/grader logic.
 */
public interface Learnable {

	Long getLearnableId();

	String getTerm();

	String getMeaning();

	String getExampleSentence();
}
