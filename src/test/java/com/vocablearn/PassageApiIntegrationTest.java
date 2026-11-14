package com.vocablearn;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PassageApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	private Cookie authCookie;

	@BeforeEach
	void registerAndAuthenticate() throws Exception {
		String email = "reader-" + System.nanoTime() + "@example.com";
		String body = """
				{"email":"%s","displayName":"Reader","password":"password123"}
				""".formatted(email);
		authCookie = mockMvc
				.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getCookie("access_token");
	}

	@Test
	void listPassagesRequiresAuthentication() throws Exception {
		mockMvc.perform(get("/api/passages")).andExpect(status().isUnauthorized());
	}

	@Test
	void listPassagesReturnsAllThirtySeededWhenAuthenticated() throws Exception {
		mockMvc.perform(get("/api/passages").cookie(authCookie))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(30));
	}

	@Test
	void passageDetailIncludesParagraphsVocabularyAndQuestionsWithoutAnswerKey() throws Exception {
		mockMvc.perform(get("/api/passages/1").cookie(authCookie))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.paragraphs.length()", greaterThanOrEqualTo(1)))
				.andExpect(jsonPath("$.vocabulary.length()", greaterThanOrEqualTo(1)))
				.andExpect(jsonPath("$.questions.length()").value(3))
				.andExpect(jsonPath("$.questions[0].correctOptionIndex").doesNotExist());
	}

	@Test
	void multipleChoiceExerciseRequiresAuthenticationToo() throws Exception {
		mockMvc.perform(get("/api/passages/1/exercises/MULTIPLE_CHOICE"))
				.andExpect(status().isUnauthorized());

		mockMvc.perform(get("/api/passages/1/exercises/MULTIPLE_CHOICE").cookie(authCookie))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.exerciseType").value("MULTIPLE_CHOICE"))
				.andExpect(jsonPath("$.items.length()", greaterThanOrEqualTo(1)));
	}
}
