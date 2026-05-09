package dequelite.financemirror_backend.app.auth.sign_up;

import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/sign-up")
@Slf4j
@RequiredArgsConstructor
public class SignUpController {
    private final SignUpService service;

    public ResponseEntity<?> signUp(
            @Valid @RequestBody SignUpRequestDto body
            ){
        return ResponseEntity.ok(null);
    }
}
