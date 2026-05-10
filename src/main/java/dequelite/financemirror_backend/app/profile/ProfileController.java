package dequelite.financemirror_backend.app.profile;

import dequelite.financemirror_backend.app.profile.dto.ProfileGraphDto;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService service;

    @QueryMapping
    public ProfileGraphDto profile(
            @AuthenticationPrincipal TokenHeaderDTO user
    ) {
        return this.service.getProfile(UUID.fromString(user.getSub()));
    }

}
