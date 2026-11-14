package com.vocablearn.seed;

import com.vocablearn.domain.QuizQuestion;
import com.vocablearn.domain.QuizSet;
import com.vocablearn.repository.QuizSetRepository;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Loads the ETL-generated quizzes-seed.json (exam-question "QUIZ" module)
 * into the database on startup. Idempotent per-set rather than all-or-
 * nothing: a set already present (matched by title) is left untouched, but
 * any new set added to the JSON later (e.g. a newly-ETL'd exam-question
 * type) still gets inserted on the next startup. Mirrors {@link
 * ConjunctionDataSeeder}/{@link ModalDataSeeder} in spirit, just for the
 * quizzes domain.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(4)
public class QuizDataSeeder implements CommandLineRunner {

	private static final String SEED_RESOURCE = "data/quizzes-seed.json";

	private final QuizSetRepository quizSetRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void run(String... args) throws Exception {
		List<SeedQuizSet> seedQuizSets;
		try (InputStream in = new ClassPathResource(SEED_RESOURCE).getInputStream()) {
			seedQuizSets = objectMapper.readValue(in, new tools.jackson.core.type.TypeReference<List<SeedQuizSet>>() {
			});
		}

		List<String> existingTitles = quizSetRepository.findAll().stream().map(QuizSet::getTitle).toList();

		int inserted = 0;
		for (SeedQuizSet seed : seedQuizSets) {
			if (existingTitles.contains(seed.title())) {
				continue;
			}
			quizSetRepository.save(toEntity(seed));
			inserted++;
		}
		log.info("Seeded {} new quiz set(s) from {} ({} already present)", inserted, SEED_RESOURCE,
				seedQuizSets.size() - inserted);
	}

	private QuizSet toEntity(SeedQuizSet seed) {
		QuizSet quizSet = new QuizSet();
		quizSet.setTitle(seed.title());
		quizSet.setDescription(seed.description());
		quizSet.setOrderIndex(seed.order());

		for (SeedQuizQuestion seedQuestion : seed.questions()) {
			QuizQuestion question = new QuizQuestion();
			question.setQuizSet(quizSet);
			question.setOrderIndex(seedQuestion.order());
			question.setExamLabel(seedQuestion.examLabel());
			question.setPromptText(seedQuestion.promptText());
			question.setTurkishTranslation(seedQuestion.turkishTranslation());
			question.setOptionA(seedQuestion.optionA());
			question.setOptionB(seedQuestion.optionB());
			question.setOptionC(seedQuestion.optionC());
			question.setOptionD(seedQuestion.optionD());
			question.setOptionE(seedQuestion.optionE());
			question.setCorrectOption(seedQuestion.correctOption());
			question.setExplanation(seedQuestion.explanation());
			quizSet.getQuestions().add(question);
		}

		return quizSet;
	}
}
