package dequelite.financemirror_backend.app.profile.user_profile;

import dequelite.financemirror_backend.app.profile.user_profile.dto.ProfileGraphDto;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final ProfileService service;

    @QueryMapping
    public ProfileGraphDto profile(
            @AuthenticationPrincipal TokenHeaderDTO user
    ) {
        log.info("GRAPHQL profile request START | userId={}", user.getSub());

        ProfileGraphDto result = this.service.getProfile(UUID.fromString(user.getSub()));

        log.info("profile: {}", result);
        return result;
    }
}
