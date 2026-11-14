package com.vocablearn.dto;

import java.util.List;

public record ParagraphDto(int order, List<SentenceDto> sentences) {
}
