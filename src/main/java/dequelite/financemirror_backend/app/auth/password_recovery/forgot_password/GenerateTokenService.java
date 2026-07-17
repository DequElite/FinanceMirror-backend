package dequelite.financemirror_backend.app.auth.password_recovery.forgot_password;

import dequelite.financemirror_backend.app.auth.password_recovery.forgot_password.dto.GenerateTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class GenerateTokenService {
    private final SecureRandom random;

    public GenerateTokenDto generateToken() {
        try {
            byte[] bytes = new byte[32];
            random.nextBytes(bytes);

            String token = HexFormat.of().formatHex(bytes);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return new GenerateTokenDto(
                    token,
                    HexFormat.of().formatHex(hashed)
            );
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
