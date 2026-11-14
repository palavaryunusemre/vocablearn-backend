package com.vocablearn.seed;

import com.vocablearn.domain.ComprehensionQuestion;
import com.vocablearn.domain.Passage;
import com.vocablearn.domain.PassageParagraph;
import com.vocablearn.domain.PassageSentence;
import com.vocablearn.domain.QuestionType;
import com.vocablearn.domain.VocabularyWord;
import com.vocablearn.repository.PassageRepository;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Loads the ETL-generated passages-seed.json into the database on startup,
 * once. Idempotent: skips entirely if any passage already exists.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PassageDataSeeder implements CommandLineRunner {

	private static final String SEED_RESOURCE = "data/passages-seed.json";

	private final PassageRepository passageRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void run(String... args) throws Exception {
		if (passageRepository.count() > 0) {
			log.info("Passages already seeded ({} rows) -- skipping seeder", passageRepository.count());
			return;
		}

		List<SeedPassage> seedPassages;
		try (InputStream in = new ClassPathResource(SEED_RESOURCE).getInputStream()) {
			seedPassages = objectMapper.readValue(in, new tools.jackson.core.type.TypeReference<List<SeedPassage>>() {
			});
		}

		for (SeedPassage seed : seedPassages) {
			passageRepository.save(toEntity(seed));
		}
		log.info("Seeded {} passages from {}", seedPassages.size(), SEED_RESOURCE);
	}

	private Passage toEntity(SeedPassage seed) {
		Passage passage = new Passage();
		passage.setTitle(seed.title());
		passage.setLevel(seed.level());
		passage.setOrderIndex(seed.order());

		for (SeedParagraph seedParagraph : seed.paragraphs()) {
			PassageParagraph paragraph = new PassageParagraph();
			paragraph.setPassage(passage);
			paragraph.setOrderIndex(seedParagraph.order());
			for (SeedSentence seedSentence : seedParagraph.sentences()) {
				PassageSentence sentence = new PassageSentence();
				sentence.setParagraph(paragraph);
				sentence.setOrderIndex(seedSentence.order());
				sentence.setEnglishText(seedSentence.english());
				sentence.setTurkishText(seedSentence.turkish());
				paragraph.getSentences().add(sentence);
			}
			passage.getParagraphs().add(paragraph);
		}

		for (SeedVocabularyWord seedWord : seed.vocabulary()) {
			VocabularyWord word = new VocabularyWord();
			word.setPassage(passage);
			word.setOrderIndex(seedWord.order());
			word.setEnglishTerm(seedWord.englishTerm());
			word.setTurkishMeaning(seedWord.turkishMeaning());
			word.setExampleSentence(blankToNull(seedWord.exampleSentence()));
			passage.getVocabulary().add(word);
		}

		for (SeedQuestion seedQuestion : seed.questions()) {
			ComprehensionQuestion question = new ComprehensionQuestion();
			question.setPassage(passage);
			question.setOrderIndex(seedQuestion.order());
			question.setQuestionType(QuestionType.valueOf(seedQuestion.type()));
			question.setQuestionText(seedQuestion.text());
			question.getOptions().addAll(seedQuestion.options());
			question.setCorrectOptionIndex(seedQuestion.correctOptionIndex());
			passage.getQuestions().add(question);
		}

		return passage;
	}

	private String blankToNull(String value) {
		return (value == null || value.isBlank()) ? null : value;
	}
}
