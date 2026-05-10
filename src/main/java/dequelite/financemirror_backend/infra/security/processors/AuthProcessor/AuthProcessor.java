package dequelite.financemirror_backend.infra.security.processors.AuthProcessor;

import dequelite.financemirror_backend.app.common.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthProcessor {
    private final JwtService jwtService;

    public TokenHeaderDTO validateToken(String authHeader, String accessTokenSecret) {
        String[] authHeaderPats = authHeader.split(" ");
        if(!authHeaderPats[0].equals("Bearer")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }

        String token = authHeaderPats[1];
        if(token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }

        Map<String, Object> decodedToken;
        try {
            decodedToken = this.jwtService.verifyToken(token, accessTokenSecret);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_UNVALID");
        }

        return TokenHeaderDTO.builder()
                .jti(decodedToken.get("jti").toString())
                .sub(decodedToken.get("sub").toString())
                .build();
    }
}
