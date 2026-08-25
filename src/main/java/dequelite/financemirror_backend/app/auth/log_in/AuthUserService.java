package dequelite.financemirror_backend.app.auth.log_in;

import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity auth(String email, String password) {
        UserEntity user = this.userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_CREDENTIALS));

        boolean isPasswordValid = this.passwordEncoder.matches(
                password,
                user.getPassword()
        );
        if(!isPasswordValid) {
            throw new ApiException(ErrorCode.INVALID_CREDENTIALS);
        }

        return user;
    }
}
