package com.vocablearn.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.vocablearn.domain.ComprehensionQuestion;
import com.vocablearn.domain.Passage;
import com.vocablearn.domain.PassageParagraph;
import com.vocablearn.domain.PassageSentence;
import com.vocablearn.domain.QuestionType;
import com.vocablearn.domain.VocabularyWord;
import com.vocablearn.dto.PassageDetailDto;
import com.vocablearn.dto.PassageSummaryDto;
import org.junit.jupiter.api.Test;

class PassageMapperTest {

	private final PassageMapper mapper = new PassageMapper();

	private Passage buildPassage() {
		Passage passage = new Passage();
		passage.setId(1L);
		passage.setOrderIndex(3);
		passage.setTitle("THE BYSTANDER EFFECT");
		passage.setLevel("C1");

		PassageParagraph paragraph = new PassageParagraph();
		paragraph.setPassage(passage);
		paragraph.setOrderIndex(0);
		PassageSentence sentence = new PassageSentence();
		sentence.setParagraph(paragraph);
		sentence.setOrderIndex(0);
		sentence.setEnglishText("The bystander effect is real.");
		sentence.setTurkishText("Seyirci etkisi gerçektir.");
		paragraph.getSentences().add(sentence);
		passage.getParagraphs().add(paragraph);

		VocabularyWord word = new VocabularyWord();
		word.setId(10L);
		word.setOrderIndex(1);
		word.setEnglishTerm("phenomenon");
		word.setTurkishMeaning("olgu");
		word.setExampleSentence("This is a phenomenon.");
		passage.getVocabulary().add(word);

		ComprehensionQuestion question = new ComprehensionQuestion();
		question.setId(20L);
		question.setOrderIndex(0);
		question.setQuestionType(QuestionType.MAIN_IDEA);
		question.setQuestionText("What is the main idea?");
		question.getOptions().addAll(java.util.List.of("a", "b", "c", "d", "e"));
		question.setCorrectOptionIndex(2);
		passage.getQuestions().add(question);

		return passage;
	}

	@Test
	void toSummary_mapsBasicFieldsAndVocabularyCount() {
		Passage passage = buildPassage();

		PassageSummaryDto summary = mapper.toSummary(passage);

		assertThat(summary.id()).isEqualTo(1L);
		assertThat(summary.order()).isEqualTo(3);
		assertThat(summary.title()).isEqualTo("THE BYSTANDER EFFECT");
		assertThat(summary.level()).isEqualTo("C1");
		assertThat(summary.vocabularyCount()).isEqualTo(1);
	}

	@Test
	void toDetail_mapsParagraphsSentencesVocabularyAndQuestions() {
		Passage passage = buildPassage();

		PassageDetailDto detail = mapper.toDetail(passage);

		assertThat(detail.paragraphs()).hasSize(1);
		assertThat(detail.paragraphs().get(0).sentences()).hasSize(1);
		assertThat(detail.paragraphs().get(0).sentences().get(0).english()).isEqualTo("The bystander effect is real.");
		assertThat(detail.paragraphs().get(0).sentences().get(0).turkish()).isEqualTo("Seyirci etkisi gerçektir.");

		assertThat(detail.vocabulary()).hasSize(1);
		assertThat(detail.vocabulary().get(0).englishTerm()).isEqualTo("phenomenon");

		assertThat(detail.questions()).hasSize(1);
		assertThat(detail.questions().get(0).questionType()).isEqualTo("MAIN_IDEA");
		assertThat(detail.questions().get(0).options()).containsExactly("a", "b", "c", "d", "e");
	}

	@Test
	void toVocabularyDto_mapsAllFieldsIncludingExampleSentence() {
		VocabularyWord word = new VocabularyWord();
		word.setId(5L);
		word.setOrderIndex(2);
		word.setEnglishTerm("offer");
		word.setTurkishMeaning("teklif etmek");
		word.setExampleSentence("They offer help.");

		var dto = mapper.toVocabularyDto(word);

		assertThat(dto.id()).isEqualTo(5L);
		assertThat(dto.order()).isEqualTo(2);
		assertThat(dto.englishTerm()).isEqualTo("offer");
		assertThat(dto.turkishMeaning()).isEqualTo("teklif etmek");
		assertThat(dto.exampleSentence()).isEqualTo("They offer help.");
	}
}
