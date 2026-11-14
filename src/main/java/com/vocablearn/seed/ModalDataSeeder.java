package com.vocablearn.seed;

import com.vocablearn.domain.ModalCategory;
import com.vocablearn.domain.ModalEntry;
import com.vocablearn.repository.ModalCategoryRepository;
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
 * Loads the ETL-generated modals-seed.json (grammar/"Modals" module) into
 * the database on startup, once. Idempotent: skips entirely if any category
 * already exists. Mirrors {@link ConjunctionDataSeeder} exactly, just for
 * the modals domain.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class ModalDataSeeder implements CommandLineRunner {

	private static final String SEED_RESOURCE = "data/modals-seed.json";

	private final ModalCategoryRepository categoryRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void run(String... args) throws Exception {
		if (categoryRepository.count() > 0) {
			log.info("Modal categories already seeded ({} rows) -- skipping seeder", categoryRepository.count());
			return;
		}

		List<SeedModalCategory> seedCategories;
		try (InputStream in = new ClassPathResource(SEED_RESOURCE).getInputStream()) {
			seedCategories = objectMapper.readValue(
					in, new tools.jackson.core.type.TypeReference<List<SeedModalCategory>>() {
					});
		}

		for (SeedModalCategory seed : seedCategories) {
			categoryRepository.save(toEntity(seed));
		}
		log.info("Seeded {} modal categories from {}", seedCategories.size(), SEED_RESOURCE);
	}

	private ModalCategory toEntity(SeedModalCategory seed) {
		ModalCategory category = new ModalCategory();
		category.setTitle(seed.title());
		category.setUsageNote(seed.usageNote());
		category.setOrderIndex(seed.order());

		for (SeedModalEntry seedEntry : seed.entries()) {
			ModalEntry entry = new ModalEntry();
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
