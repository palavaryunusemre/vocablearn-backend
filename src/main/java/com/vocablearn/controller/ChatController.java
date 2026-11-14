package com.vocablearn.controller;

import com.vocablearn.dto.ChatRequestDto;
import com.vocablearn.dto.ChatResponseDto;
import com.vocablearn.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	@PostMapping
	public ChatResponseDto ask(@Valid @RequestBody ChatRequestDto request) {
		return chatService.ask(request.question(), request.context());
	}
}
