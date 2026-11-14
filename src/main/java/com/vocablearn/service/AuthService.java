package com.vocablearn.service;

import com.vocablearn.dto.AuthResultDto;
import com.vocablearn.dto.LoginRequestDto;
import com.vocablearn.dto.RegisterRequestDto;

public interface AuthService {

	AuthResultDto register(RegisterRequestDto request);

	AuthResultDto login(LoginRequestDto request);
}
