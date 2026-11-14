package com.vocablearn.seed;

import com.vocablearn.domain.ConjunctionCategory;
import com.vocablearn.domain.ConjunctionEntry;
import com.vocablearn.repository.ConjunctionCategoryRepository;
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
 * Loads the ETL-generated conjunctions-seed.json (grammar/"Baglaclar"
 * module) into the database on startup, once. Idempotent: skips entirely if
 * any category already exists. Mirrors {@link PassageDataSeeder} exactly,
 * just for the conjunctions domain.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class ConjunctionDataSeeder implements CommandLineRunner {

	private static final String SEED_RESOURCE = "data/conjunctions-seed.json";

	private final ConjunctionCategoryRepository categoryRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void run(String... args) throws Exception {
		if (categoryRepository.count() > 0) {
			log.info("Conjunction categories already seeded ({} rows) -- skipping seeder", categoryRepository.count());
			return;
		}

		List<SeedConjunctionCategory> seedCategories;
		try (InputStream in = new ClassPathResource(SEED_RESOURCE).getInputStream()) {
			seedCategories = objectMapper.readValue(
					in, new tools.jackson.core.type.TypeReference<List<SeedConjunctionCategory>>() {
					});
		}

		for (SeedConjunctionCategory seed : seedCategories) {
			categoryRepository.save(toEntity(seed));
		}
		log.info("Seeded {} conjunction categories from {}", seedCategories.size(), SEED_RESOURCE);
	}

	private ConjunctionCategory toEntity(SeedConjunctionCategory seed) {
		ConjunctionCategory category = new ConjunctionCategory();
		category.setTitle(seed.title());
		category.setUsageNote(seed.usageNote());
		category.setOrderIndex(seed.order());

		for (SeedConjunctionEntry seedEntry : seed.entries()) {
			ConjunctionEntry entry = new ConjunctionEntry();
			entry.setCategory(category);
			entry.setOrderIndex(seedEntry.order());
			entry.setEnglishTerm(seedEntry.term());
			entry.setTurkishMeaning(seedEntry.meaning());
			entry.setExampleSentence(blankToNull(seedEntry.exampleSentence()));
			category.getEntries().add(entry);
		}

		return category;
	}

	private String blankToNull(String value) {
		return (value == null || value.isBlank()) ? null : value;
	}
}
