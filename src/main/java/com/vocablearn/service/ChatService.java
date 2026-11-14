package com.vocablearn.service;

import com.vocablearn.dto.ChatResponseDto;

public interface ChatService {

	/**
	 * @param context optional, free-text description of what the learner is
	 *                currently looking at (e.g. the exact quiz question);
	 *                may be null/blank.
	 */
	ChatResponseDto ask(String question, String context);
}
