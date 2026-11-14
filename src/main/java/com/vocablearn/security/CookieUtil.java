package com.vocablearn.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {

	public static final String AUTH_COOKIE_NAME = "access_token";

	private final JwtTokenProvider jwtTokenProvider;

	public void setAuthCookie(HttpServletResponse response, String token) {
		ResponseCookie cookie = ResponseCookie.from(AUTH_COOKIE_NAME, token)
				.httpOnly(true)
				.secure(false) // set true once served over HTTPS in production
				.sameSite("Lax")
				.path("/")
				.maxAge(jwtTokenProvider.expirationSeconds())
				.build();
		response.addHeader("Set-Cookie", cookie.toString());
	}

	public void clearAuthCookie(HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie.from(AUTH_COOKIE_NAME, "")
				.httpOnly(true)
				.secure(false)
				.sameSite("Lax")
				.path("/")
				.maxAge(0)
				.build();
		response.addHeader("Set-Cookie", cookie.toString());
	}

	public String readAuthCookie(Cookie[] cookies) {
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
