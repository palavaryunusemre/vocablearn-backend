package com.vocablearn.exercise;

import com.vocablearn.domain.ModalEntry;
import com.vocablearn.dto.ExerciseAnswerDto;
import com.vocablearn.dto.ExerciseDto;
import com.vocablearn.dto.ExerciseItemDto;
import com.vocablearn.dto.ExerciseOptionDto;
import com.vocablearn.dto.ExerciseResultDto;
import com.vocablearn.dto.ExerciseSubmissionDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * "Hangi türe ait?" cross-category quiz for the MODALS module -- mirrors
 * {@link CategoryQuizGenerator} exactly, just over {@link ModalEntry} instead
 * of {@code ConjunctionEntry}. Kept as its own class rather than a shared
 * generic because the two domains' category title conventions (and the
 * frontend routes that consume them) are already independent; duplicating
 * this small, self-contained generator is cheaper than forcing a shared
 * abstraction across two unrelated grammar modules.
 */
@Component
public class ModalCategoryQuizGenerator {

	private static final int OPTION_COUNT = 4;
	private static final int MAX_ATTEMPTS_PER_QUESTION = 50;

	/** Strips any trailing Turkish gloss in parentheses, e.g. "ABILITY (Yetenek)" -&gt; "ABILITY". */
	static String groupName(ModalEntry entry) {
		String title = entry.getCategory().getTitle();
		int parenIndex = title.indexOf('(');
		return (parenIndex >= 0 ? title.substring(0, parenIndex) : title).trim();
	}

	public ExerciseDto generate(List<ModalEntry> allEntries, int questionCount) {
		Map<String, List<ModalEntry>> byGroup = allEntries.stream()
				.collect(Collectors.groupingBy(ModalCategoryQuizGenerator::groupName));
		List<String> groupNames = new ArrayList<>(byGroup.keySet());

		Set<Long> usedCorrectIds = new HashSet<>();

		List<ExerciseItemDto> questions = new ArrayList<>();
		for (int i = 0; i < questionCount && !groupNames.isEmpty(); i++) {
			buildQuestion(byGroup, groupNames, allEntries, usedCorrectIds).ifPresent(questions::add);
		}
		return ExerciseDto.ofItems("CATEGORY_QUIZ", questions);
	}

	private Optional<ExerciseItemDto> buildQuestion(
			Map<String, List<ModalEntry>> byGroup,
			List<String> groupNames,
			List<ModalEntry> allEntries,
			Set<Long> usedCorrectIds) {
		for (int attempt = 0; attempt < MAX_ATTEMPTS_PER_QUESTION; attempt++) {
			String targetGroup = groupNames.get(ThreadLocalRandom.current().nextInt(groupNames.size()));
			List<ModalEntry> inGroup = byGroup.get(targetGroup);
			List<ModalEntry> outOfGroup = allEntries.stream()
					.filter(e -> !groupName(e).equals(targetGroup))
					.toList();

			boolean askIsNotMember = inGroup.size() >= OPTION_COUNT - 1 && ThreadLocalRandom.current().nextBoolean();
			List<ModalEntry> correctPool = askIsNotMember ? outOfGroup : inGroup;
			List<ModalEntry> unusedCorrectPool =
					correctPool.stream().filter(e -> !usedCorrectIds.contains(e.getId())).toList();
			if (unusedCorrectPool.isEmpty()) {
				continue;
			}

			ModalEntry correct = unusedCorrectPool.get(ThreadLocalRandom.current().nextInt(unusedCorrectPool.size()));
			List<ModalEntry> distractors =
					pickRandomDistinct(askIsNotMember ? inGroup : outOfGroup, OPTION_COUNT - 1, correct);
			String questionText = askIsNotMember
					? "Aşağıdakilerden hangisi " + targetGroup + " türü DEĞİLDİR?"
					: "Aşağıdakilerden hangisi " + targetGroup + " türüdür?";

			usedCorrectIds.add(correct.getId());

			List<ExerciseOptionDto> options = new ArrayList<>();
			options.add(new ExerciseOptionDto(correct.getId().toString(), correct.getEnglishTerm()));
			for (ModalEntry distractor : distractors) {
				options.add(new ExerciseOptionDto(distractor.getId().toString(), distractor.getEnglishTerm()));
			}
			Collections.shuffle(options);

			return Optional.of(new ExerciseItemDto(correct.getId().toString(), questionText, options));
		}
		return Optional.empty();
	}

	private List<ModalEntry> pickRandomDistinct(List<ModalEntry> pool, int count, ModalEntry exclude) {
		List<ModalEntry> candidates = new ArrayList<>(pool);
		candidates.remove(exclude);
		Collections.shuffle(candidates);
		return candidates.subList(0, Math.min(count, candidates.size()));
	}

	public ExerciseResultDto grade(ExerciseSubmissionDto submission) {
		List<ExerciseAnswerDto> answers = submission.answers();
		return IdEqualityGrading.gradeAnswers(answers);
	}
}
