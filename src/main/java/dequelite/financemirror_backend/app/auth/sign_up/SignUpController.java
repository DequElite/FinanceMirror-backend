package dequelite.financemirror_backend.app.auth.sign_up;

import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpRequestDto;
import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpResponseDto;
import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpServiceResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth/sign-up")
@Slf4j
@RequiredArgsConstructor
public class SignUpController {
    private final SignUpService service;

    @PostMapping
    public ResponseEntity<SignUpResponseDto> signUp(
            @Valid @RequestBody SignUpRequestDto body
            ){
          this.service.signUp(body);

          return ResponseEntity.ok()
                  .body(
                          SignUpResponseDto.builder().message("Verification sent").build()
                  );
    }
}
