package dequelite.financemirror_backend.app.auth.sign_up;

import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpRequestDto;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
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
public class RegisterUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity createUser(SignUpRequestDto body) {
        Optional<UserEntity> userEntity = this.userRepository.findByEmail(body.getEmail());
        if(userEntity.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        ProfileEntity profile = ProfileEntity.builder()
                .monthSalary(body.getSalary())
                .currency(body.getCurrency())
                .balance(0)
                .build();

        UserEntity newUser = UserEntity.builder()
                .profile(profile)
                .email(body.getEmail())
                .password(this.passwordEncoder.encode(body.getPassword()))
                .build();

        return this.userRepository.save(newUser);
    }

}
