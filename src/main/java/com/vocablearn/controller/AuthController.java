package com.vocablearn.controller;

import com.vocablearn.dto.AuthResultDto;
import com.vocablearn.dto.AuthUserDto;
import com.vocablearn.dto.LoginRequestDto;
import com.vocablearn.dto.RegisterRequestDto;
import com.vocablearn.security.AppUserPrincipal;
import com.vocablearn.security.CookieUtil;
import com.vocablearn.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final CookieUtil cookieUtil;

	@PostMapping("/register")
	public AuthUserDto register(@Valid @RequestBody RegisterRequestDto request, HttpServletResponse response) {
		AuthResultDto result = authService.register(request);
		cookieUtil.setAuthCookie(response, result.token());
		return result.user();
	}

	@PostMapping("/login")
	public AuthUserDto login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response) {
		AuthResultDto result = authService.login(request);
		cookieUtil.setAuthCookie(response, result.token());
		return result.user();
	}

	@PostMapping("/logout")
	public void logout(HttpServletResponse response) {
		cookieUtil.clearAuthCookie(response);
	}

	@GetMapping("/me")
	public AuthUserDto me(@AuthenticationPrincipal AppUserPrincipal principal) {
		var user = principal.getAppUser();
		return new AuthUserDto(user.getId(), user.getEmail(), user.getDisplayName(), user.getRole().name());
	}
}
