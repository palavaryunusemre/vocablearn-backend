package com.vocablearn.service.impl;

import com.vocablearn.domain.AppUser;
import com.vocablearn.domain.UserRole;
import com.vocablearn.dto.AuthResultDto;
import com.vocablearn.dto.AuthUserDto;
import com.vocablearn.dto.LoginRequestDto;
import com.vocablearn.dto.RegisterRequestDto;
import com.vocablearn.exception.BadRequestException;
import com.vocablearn.repository.AppUserRepository;
import com.vocablearn.security.JwtTokenProvider;
import com.vocablearn.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManager authenticationManager;

	@Override
	@Transactional
	public AuthResultDto register(RegisterRequestDto request) {
		if (appUserRepository.existsByEmail(request.email())) {
			throw new BadRequestException("An account with this email already exists");
		}
		AppUser user = new AppUser();
		user.setEmail(request.email());
		user.setDisplayName(request.displayName());
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setRole(UserRole.USER);
		appUserRepository.save(user);

		String token = jwtTokenProvider.generateToken(user);
		return new AuthResultDto(token, toDto(user));
	}

	@Override
	public AuthResultDto login(LoginRequestDto request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password()));

		AppUser user = appUserRepository.findByEmail(request.email())
				.orElseThrow(() -> new BadRequestException("Invalid email or password"));

		String token = jwtTokenProvider.generateToken(user);
		return new AuthResultDto(token, toDto(user));
	}

	private AuthUserDto toDto(AppUser user) {
		return new AuthUserDto(user.getId(), user.getEmail(), user.getDisplayName(), user.getRole().name());
	}
}
