package dequelite.financemirror_backend.app.auth.log_in;

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
        Optional<UserEntity> user = this.userRepository.findByEmail(email);
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        boolean isPasswordValid = this.passwordEncoder.matches(
                password,
                user.get().getPassword()
        );
        if(!isPasswordValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return user.get();
    }
}
