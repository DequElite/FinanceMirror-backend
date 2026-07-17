package dequelite.financemirror_backend.app.profile.user_profile;

import dequelite.financemirror_backend.app.profile.user_profile.dto.ProfileGraphDto;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
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
        Optional<ProfileEntity> profile = this.profileRepository.findByUser_Id(userId);

        if (profile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile not found");
        }

        return this.mapper.toDto(profile.get());
    }
}