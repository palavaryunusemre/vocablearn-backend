package com.vocablearn.mapper;

import com.vocablearn.domain.ComprehensionQuestion;
import com.vocablearn.domain.Passage;
import com.vocablearn.domain.PassageParagraph;
import com.vocablearn.domain.PassageSentence;
import com.vocablearn.domain.VocabularyWord;
import com.vocablearn.dto.ComprehensionQuestionDto;
import com.vocablearn.dto.ParagraphDto;
import com.vocablearn.dto.PassageDetailDto;
import com.vocablearn.dto.PassageSummaryDto;
import com.vocablearn.dto.SentenceDto;
import com.vocablearn.dto.VocabularyWordDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PassageMapper {

	public PassageSummaryDto toSummary(Passage passage) {
		return new PassageSummaryDto(
				passage.getId(),
				passage.getOrderIndex(),
				passage.getTitle(),
				passage.getLevel(),
				passage.getVocabulary().size());
	}

	public PassageDetailDto toDetail(Passage passage) {
		return new PassageDetailDto(
				passage.getId(),
				passage.getOrderIndex(),
				passage.getTitle(),
				passage.getLevel(),
				passage.getParagraphs().stream().map(this::toParagraphDto).toList(),
				passage.getVocabulary().stream().map(this::toVocabularyDto).toList(),
				passage.getQuestions().stream().map(this::toQuestionDto).toList());
	}

	private ParagraphDto toParagraphDto(PassageParagraph paragraph) {
		List<SentenceDto> sentences = paragraph.getSentences().stream()
				.map(this::toSentenceDto)
				.toList();
		return new ParagraphDto(paragraph.getOrderIndex(), sentences);
	}

	private SentenceDto toSentenceDto(PassageSentence sentence) {
		return new SentenceDto(sentence.getOrderIndex(), sentence.getEnglishText(), sentence.getTurkishText());
	}

	public VocabularyWordDto toVocabularyDto(VocabularyWord word) {
		return new VocabularyWordDto(
				word.getId(),
				word.getOrderIndex(),
				word.getEnglishTerm(),
				word.getTurkishMeaning(),
				word.getExampleSentence());
	}

	private ComprehensionQuestionDto toQuestionDto(ComprehensionQuestion question) {
		return new ComprehensionQuestionDto(
				question.getId(),
				question.getOrderIndex(),
				question.getQuestionType().name(),
				question.getQuestionText(),
				List.copyOf(question.getOptions()));
	}
}
