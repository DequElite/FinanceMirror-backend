package dequelite.financemirror_backend.app.profile;

import dequelite.financemirror_backend.app.profile.dto.ProfileGraphDto;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
import dequelite.financemirror_backend.domain.user.UserEntity;
import dequelite.financemirror_backend.domain.user.UserRepository;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileMapper mapper;

    public ProfileGraphDto getProfile(UUID userId) {
        log.info("SERVICE getProfile START | userId={}", userId);

        Optional<ProfileEntity> profile = this.profileRepository.findByUser_Id(userId);

        if (profile.isEmpty()) {
            log.warn("PROFILE NOT FOUND | userId={}", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile not found");
        }

        log.info("PROFILE FOUND | userId={}", userId);

        ProfileGraphDto dto = this.mapper.toDto(profile.get());

        log.info("SERVICE getProfile END | userId={}", userId);
        return dto;
    }
}