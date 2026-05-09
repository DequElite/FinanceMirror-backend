package dequelite.financemirror_backend.app.auth.sign_up;

import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpRequestDto;
import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpServiceResponseDto;
import dequelite.financemirror_backend.app.common.jwt.JwtService;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.session.SessionEntity;
import dequelite.financemirror_backend.domain.session.SessionRepository;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${jwt.secret.session}")
    private String accessTokenSecret;
    @Value("${jwt.ttl.session}")
    private long accessTokenTtl;

    @Value("${jwt.secret.refresh}")
    private String refreshTokenSecret;
    @Value("${jwt.ttl.refresh}")
    private long refreshTokenTtl;

    public SignUpServiceResponseDto signUp(SignUpRequestDto body) {
        Optional<UserEntity> userEntity = this.userRepository.findByEmail(body.getEmail());
        if(userEntity.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        ProfileEntity profile = ProfileEntity.builder()
                .monthSalary(body.getSalary())
                .build();

        UserEntity newUser = UserEntity.builder()
                .profile(profile)
                .email(body.getEmail())
                .password(this.passwordEncoder.encode(body.getPassword()))
                .build();

        this.userRepository.save(newUser);

        String accessTokenId = UUID.randomUUID().toString();
        String accessToken = this.jwtService.generateToken(
                newUser.getId().toString(),
                accessTokenId,
                Map.of(),
                accessTokenSecret,
                accessTokenTtl
        );

        String refreshToken = this.jwtService.generateToken(
                newUser.getId().toString(),
                null,
                null,
                refreshTokenSecret,
                refreshTokenTtl
        );
        String hashedRefreshToken = this.passwordEncoder.encode(refreshToken);
        LocalDateTime refreshTokenExpiration = LocalDateTime.now().plusMinutes(refreshTokenTtl/1000);

        SessionEntity session = SessionEntity.builder()
                .user(newUser)
                .refreshToken(hashedRefreshToken)
                .refreshTokenExpiresAt(refreshTokenExpiration)
                .accessTokenId(accessTokenId)
                .latestLogin(LocalDateTime.now())
                .build();

        this.sessionRepository.save(session);

        return SignUpServiceResponseDto.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .refreshTokenExpiration(refreshTokenExpiration)
                .build();
    }

}
