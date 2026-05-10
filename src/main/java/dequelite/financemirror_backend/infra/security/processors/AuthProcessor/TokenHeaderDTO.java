package dequelite.financemirror_backend.infra.security.processors.AuthProcessor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenHeaderDTO {
    private String jti;
    private String sub;
}
