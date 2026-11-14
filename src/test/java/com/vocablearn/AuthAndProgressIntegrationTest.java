package com.vocablearn;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthAndProgressIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void registerLoginAndSubmitExerciseUpdatesProgress() throws Exception {
		String registerBody = """
				{"email":"learner@example.com","displayName":"Test Learner","password":"password123"}
				""";

		MockHttpServletResponse registerResponse = mockMvc
				.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(registerBody))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse();

		Cookie authCookie = registerResponse.getCookie("access_token");
		org.junit.jupiter.api.Assertions.assertNotNull(authCookie, "register should set the access_token cookie");

		// Progress should exist (even if empty) for a freshly registered, authenticated user.
		mockMvc.perform(get("/api/progress/me").cookie(authCookie))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.passages.length()").value(30));

		// Without the cookie, progress must be rejected.
		mockMvc.perform(get("/api/progress/me")).andExpect(status().isUnauthorized());
	}
}
