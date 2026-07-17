package dequelite.financemirror_backend.app.ai;

import com.fasterxml.jackson.databind.JsonNode;
import dequelite.financemirror_backend.app.ai.dto.AiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;

    public AiResponseDto ask(
            String role,
            String prompt
    ) {
        JsonNode response = this.chatClient.prompt()
                .system(role)
                .user(prompt)
                .call()
                .entity(JsonNode.class);

        return new AiResponseDto(
                true,
                response
        );
    }

}
