package com.vocablearn.exercise;

import com.vocablearn.domain.ConjunctionEntry;
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
 * A different kind of quiz from the per-category Strategy games: instead of
 * term&lt;-&gt;meaning within one category, this tests whether the learner
 * can place a term into its broader conjunction group ("Hangisi SEBEP
 * bağlacı değildir?" / "Hangisi ZITLIK bağlacıdır?") -- spanning every
 * category at once. That cross-category scope doesn't fit the per-category
 * {@link ExerciseGenerator} contract (which only ever sees one category's
 * {@link com.vocablearn.domain.Learnable} items), so this is a standalone
 * component rather than another Strategy implementation; grading reuses the
 * same id-equality trick as {@link IdEqualityGrading}.
 */
@Component
public class CategoryQuizGenerator {

	private static final int OPTION_COUNT = 4;
	private static final int MAX_ATTEMPTS_PER_QUESTION = 50;

	/**
	 * Several DB categories are sub-variants of the same broader group
	 * (e.g. "ZITLIK BAĞLAÇLARI (-e rağmen · Tam Cümle)" and "ZITLIK
	 * BAĞLAÇLARI (aksine, öte yandan · İsim)" are both "ZITLIK BAĞLAÇLARI").
	 * Grouping by the title's prefix before "(" is what the quiz questions
	 * are actually asked about -- grading by the exact category row would
	 * wrongly mark a same-group sub-category entry as "not a member".
	 */
	static String groupName(ConjunctionEntry entry) {
		String title = entry.getCategory().getTitle();
		int parenIndex = title.indexOf('(');
		return (parenIndex >= 0 ? title.substring(0, parenIndex) : title).trim();
	}

	public ExerciseDto generate(List<ConjunctionEntry> allEntries, int questionCount) {
		Map<String, List<ConjunctionEntry>> byGroup = allEntries.stream()
				.collect(Collectors.groupingBy(CategoryQuizGenerator::groupName));
		List<String> groupNames = new ArrayList<>(byGroup.keySet());

		// The frontend keys answers by itemId (the correct entry's id, per the
		// id-equality grading trick) -- the same entry being picked as the
		// "correct" answer for two different questions would collapse both
		// onto the same answer-map key client-side, so this must never
		// happen. If a slot genuinely can't find an unused correct entry
		// within a reasonable number of attempts (e.g. requesting more
		// questions than there are entries), that slot is skipped rather
		// than producing a colliding question.
		Set<Long> usedCorrectIds = new HashSet<>();

		List<ExerciseItemDto> questions = new ArrayList<>();
		for (int i = 0; i < questionCount && !groupNames.isEmpty(); i++) {
			buildQuestion(byGroup, groupNames, allEntries, usedCorrectIds).ifPresent(questions::add);
		}
		return ExerciseDto.ofItems("CATEGORY_QUIZ", questions);
	}

	private Optional<ExerciseItemDto> buildQuestion(
			Map<String, List<ConjunctionEntry>> byGroup,
			List<String> groupNames,
			List<ConjunctionEntry> allEntries,
			Set<Long> usedCorrectIds) {
		for (int attempt = 0; attempt < MAX_ATTEMPTS_PER_QUESTION; attempt++) {
			String targetGroup = groupNames.get(ThreadLocalRandom.current().nextInt(groupNames.size()));
			List<ConjunctionEntry> inGroup = byGroup.get(targetGroup);
			List<ConjunctionEntry> outOfGroup = allEntries.stream()
					.filter(e -> !groupName(e).equals(targetGroup))
					.toList();

			// "is NOT a member" needs 3 same-group entries to contrast against; fall back to "IS" if the group is too small.
			boolean askIsNotMember = inGroup.size() >= OPTION_COUNT - 1 && ThreadLocalRandom.current().nextBoolean();
			List<ConjunctionEntry> correctPool = askIsNotMember ? outOfGroup : inGroup;
			List<ConjunctionEntry> unusedCorrectPool =
					correctPool.stream().filter(e -> !usedCorrectIds.contains(e.getId())).toList();
			if (unusedCorrectPool.isEmpty()) {
				continue;
			}

			ConjunctionEntry correct = unusedCorrectPool.get(ThreadLocalRandom.current().nextInt(unusedCorrectPool.size()));
			List<ConjunctionEntry> distractors =
					pickRandomDistinct(askIsNotMember ? inGroup : outOfGroup, OPTION_COUNT - 1, correct);
			String questionText = askIsNotMember
					? "Aşağıdakilerden hangisi " + targetGroup + " DEĞİLDİR?"
					: "Aşağıdakilerden hangisi " + targetGroup + "dır?";

			usedCorrectIds.add(correct.getId());

			List<ExerciseOptionDto> options = new ArrayList<>();
			options.add(new ExerciseOptionDto(correct.getId().toString(), correct.getEnglishTerm()));
			for (ConjunctionEntry distractor : distractors) {
				options.add(new ExerciseOptionDto(distractor.getId().toString(), distractor.getEnglishTerm()));
			}
			Collections.shuffle(options);

			return Optional.of(new ExerciseItemDto(correct.getId().toString(), questionText, options));
		}
		return Optional.empty();
	}

	private List<ConjunctionEntry> pickRandomDistinct(List<ConjunctionEntry> pool, int count, ConjunctionEntry exclude) {
		List<ConjunctionEntry> candidates = new ArrayList<>(pool);
		candidates.remove(exclude);
		Collections.shuffle(candidates);
		return candidates.subList(0, Math.min(count, candidates.size()));
	}

	public ExerciseResultDto grade(ExerciseSubmissionDto submission) {
		List<ExerciseAnswerDto> answers = submission.answers();
		return IdEqualityGrading.gradeAnswers(answers);
	}
}
