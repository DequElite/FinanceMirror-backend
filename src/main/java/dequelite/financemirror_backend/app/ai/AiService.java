package dequelite.financemirror_backend.app.ai;

import com.fasterxml.jackson.databind.JsonNode;
import dequelite.financemirror_backend.app.ai.dto.AiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final ChatClient chatClient;

    public AiResponseDto ask(
            String role,
            String prompt
    ) {
        String rawResponse = this.chatClient.prompt()
                .system(role)
                .user(prompt)
                .call()
                .content();

        log.info("AI RAW RESPONSE: {}", rawResponse);

        JsonNode response = this.chatClient.prompt()
                .system(role)
                .user(prompt)
                .call()
                .entity(JsonNode.class);

        log.info("AI JSON RESPONSE: {}", response);

        return new AiResponseDto(
                true,
                response
        );
    }

}
