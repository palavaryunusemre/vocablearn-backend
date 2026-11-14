package com.vocablearn.seed;

import java.util.List;

public record SeedParagraph(int order, List<SeedSentence> sentences) {
}
