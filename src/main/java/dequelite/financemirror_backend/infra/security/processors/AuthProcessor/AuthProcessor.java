package dequelite.financemirror_backend.infra.security.processors.AuthProcessor;

import dequelite.financemirror_backend.app.common.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthProcessor {
    private final JwtService jwtService;

    public TokenHeaderDTO validateToken(
            String token,
            String accessTokenSecret
    ) {

        if(token.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "UNAUTHORIZED"
            );
        }

        Map<String, Object> decodedToken;

        try {
            decodedToken =
                    this.jwtService.verifyToken(token, accessTokenSecret);

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "TOKEN_INVALID"
            );
        }

        log.info("dds: {}", decodedToken);

        return TokenHeaderDTO.builder()
                .jti(decodedToken.get("jti").toString())
                .sub(decodedToken.get("sub").toString())
                .build();
    }
}
