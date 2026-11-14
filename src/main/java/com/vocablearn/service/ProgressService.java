package com.vocablearn.service;

import com.vocablearn.dto.ProgressDto;

public interface ProgressService {

	ProgressDto getProgress(Long userId);
}
