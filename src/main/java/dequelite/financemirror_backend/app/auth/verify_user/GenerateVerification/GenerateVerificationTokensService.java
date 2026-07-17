package dequelite.financemirror_backend.app.auth.verify_user.GenerateVerification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class GenerateVerificationTokensService {
    public String generateToken () {
        byte[] bytes = new byte[32];

        random.nextBytes(bytes);

        return HexFormat.of().formatHex(bytes);
    }

    public String generateCode () {
        int code = random.nextInt(900000) + 100000;

        return String.valueOf(code);
    }
}
