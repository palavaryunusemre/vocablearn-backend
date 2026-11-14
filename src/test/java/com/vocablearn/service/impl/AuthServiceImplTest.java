package com.vocablearn.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vocablearn.domain.AppUser;
import com.vocablearn.domain.UserRole;
import com.vocablearn.dto.AuthResultDto;
import com.vocablearn.dto.LoginRequestDto;
import com.vocablearn.dto.RegisterRequestDto;
import com.vocablearn.exception.BadRequestException;
import com.vocablearn.repository.AppUserRepository;
import com.vocablearn.security.JwtTokenProvider;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

	@Mock
	private AppUserRepository appUserRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private AuthenticationManager authenticationManager;

	@InjectMocks
	private AuthServiceImpl authService;

	@Test
	void register_newEmail_hashesPasswordAndIssuesToken() {
		RegisterRequestDto request = new RegisterRequestDto("new@example.com", "New User", "password123");
		when(appUserRepository.existsByEmail("new@example.com")).thenReturn(false);
		when(passwordEncoder.encode("password123")).thenReturn("hashed");
		when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");

		AuthResultDto result = authService.register(request);

		assertThat(result.token()).isEqualTo("jwt-token");
		assertThat(result.user().email()).isEqualTo("new@example.com");
		assertThat(result.user().displayName()).isEqualTo("New User");
		assertThat(result.user().role()).isEqualTo("USER");

		ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
		verify(appUserRepository).save(captor.capture());
		AppUser saved = captor.getValue();
		assertThat(saved.getPasswordHash()).isEqualTo("hashed");
		assertThat(saved.getRole()).isEqualTo(UserRole.USER);
	}

	@Test
	void register_existingEmail_throwsBadRequestWithoutSaving() {
		RegisterRequestDto request = new RegisterRequestDto("taken@example.com", "Someone", "password123");
		when(appUserRepository.existsByEmail("taken@example.com")).thenReturn(true);

		assertThatThrownBy(() -> authService.register(request)).isInstanceOf(BadRequestException.class);

		verify(appUserRepository, never()).save(any());
	}

	@Test
	void login_validCredentials_authenticatesAndIssuesToken() {
		LoginRequestDto request = new LoginRequestDto("user@example.com", "password123");
		AppUser user = new AppUser();
		user.setId(1L);
		user.setEmail("user@example.com");
		user.setDisplayName("User");
		user.setRole(UserRole.USER);
		when(appUserRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
		when(jwtTokenProvider.generateToken(user)).thenReturn("jwt-token");

		AuthResultDto result = authService.login(request);

		assertThat(result.token()).isEqualTo("jwt-token");
		assertThat(result.user().email()).isEqualTo("user@example.com");
		verify(authenticationManager).authenticate(any());
	}

	@Test
	void login_authenticationManagerRejects_propagatesException() {
		LoginRequestDto request = new LoginRequestDto("user@example.com", "wrong-password");
		when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad credentials"));

		assertThatThrownBy(() -> authService.login(request)).isInstanceOf(BadCredentialsException.class);

		verify(appUserRepository, never()).findByEmail(any());
	}
}
