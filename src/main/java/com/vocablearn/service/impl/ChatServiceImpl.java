package com.vocablearn.service.impl;

import com.vocablearn.dto.ChatResponseDto;
import com.vocablearn.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * Deliberately no retrieval/RAG here: an earlier version used a
 * RetrievalAugmentationAdvisor backed by a pgvector store, but that
 * advisor's own prompt template instructs the model to answer *only* from
 * whatever it finds in the vector store and say "I don't know" otherwise --
 * which kept discarding both general knowledge and the exact quiz-question
 * context the frontend sends. This just asks the Groq-backed ChatModel
 * directly (see ChatModel bean config in application.yaml: spring.ai.openai.*
 * pointed at Groq's OpenAI-compatible endpoint).
 */
@Service
public class ChatServiceImpl implements ChatService {

	private static final String SYSTEM_PROMPT = """
			Sen YDS/YOKDIL sinavlarina calisan ogrencilere yardimci olan bir ingilizce ogretmeni asistanisin.

			KAPSAM: Sadece Ingilizce dilbilgisi, kelime bilgisi, ceviri ve YDS/YOKDIL sinav sorulariyla
			ilgili sorulari cevapla. Bu kapsamin disinda bir soru gelirse (sohbet, genel kultur, kod yazma,
			ilgisiz konular vb.), kisa ve kibarca "Ben sadece Ingilizce/sinav sorulariyla ilgili sorulara
			yardimci olabilirim." de ve baska hicbir sey ekleme.

			CEVAP TARZI - "OSYM'nin Sifreleri" mantiginda, nokta atisi:
			- 1-3 KISA cumle. Kesinlikle madde listesi, tablo, baslik veya uzun paragraf kullanma.
			- Sadece SORULAN noktaya odaklan (orn. "neden V2" sorulduysa SADECE o kurala deg,
			  cumlenin tamamini veya diger secenekleri tekrar aciklama).
			- Dogrudan kurala/ipucuna git: "X oldugu icin Y kullanilir" formatinda, kisa ve kesin.
			- Yanlis secenekleri tek tek analiz etme; sadece dogru cevabin ardindaki mantigi ver.
			- Ogrencinin su anda ekranda gordugu bir sinav sorusu sana verildiyse, onu guvenilir bilgi
			  kaynagi olarak kullan ama soruyu/secenekleri aynen tekrar etme, dogrudan cevaba gec.
			""";

	private final ChatClient chatClient;

	public ChatServiceImpl(ChatModel chatModel) {
		this.chatClient = ChatClient.builder(chatModel)
				.defaultSystem(SYSTEM_PROMPT)
				.build();
	}

	@Override
	public ChatResponseDto ask(String question, String context) {
		String userMessage = (context == null || context.isBlank())
				? question
				: "Sinav sorusu:\n" + context + "\n\nOgrencinin sorusu: " + question;

		String answer = chatClient.prompt()
				.user(userMessage)
				.call()
				.content();

		return new ChatResponseDto(answer);
	}
}
