package dequelite.financemirror_backend.app.auth.sign_up;

import dequelite.financemirror_backend.app.auth.sign_up.dto.SignUpRequestDto;
import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.subscription.SubscriptionEntity;
import dequelite.financemirror_backend.domain.subscription.SubscriptionStatus;
import dequelite.financemirror_backend.domain.subscription.SubscriptionType;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${subscription.trial.expires-at}")
    private Long subscriptionExpiresAt;

    public UserEntity createUser(SignUpRequestDto body) {
        Optional<UserEntity> userEntity = this.userRepository.findByEmail(body.getEmail());
        if(userEntity.isPresent()){
            throw new ApiException(ErrorCode.EMAIL_ALREADY_USED);
        }

        ProfileEntity profile = ProfileEntity.builder()
                .monthSalary(body.getSalary())
                .currency(body.getCurrency())
                .balance(0)
                .build();

        SubscriptionEntity newSubscription = SubscriptionEntity.builder()
                .type(SubscriptionType.TRIAL)
                .status(SubscriptionStatus.ACTIVE)
                .started_at(LocalDate.now())
                .updated_at(LocalDate.now())
                .expires_at(LocalDate.now().plusDays(subscriptionExpiresAt))
                .build();

        UserEntity newUser = UserEntity.builder()
                .profile(profile)
                .subscription(newSubscription)
                .email(body.getEmail())
                .password(this.passwordEncoder.encode(body.getPassword()))
                .verified(false)
                .build();

        return this.userRepository.save(newUser);
    }

}
