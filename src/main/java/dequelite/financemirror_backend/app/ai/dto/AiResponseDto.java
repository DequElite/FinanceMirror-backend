package dequelite.financemirror_backend.app.ai.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record AiResponseDto (
        boolean success,
        JsonNode data
) {
}
