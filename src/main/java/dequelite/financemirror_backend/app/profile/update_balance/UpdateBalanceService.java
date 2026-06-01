package dequelite.financemirror_backend.app.profile.update_balance;

import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateBalanceService {
    private final ProfileRepository profileRepository;

    public void updateBalance(int amount, UUID userId) {
        Optional<ProfileEntity> foundProfile = this.profileRepository.findByUser_Id(userId);
        if (foundProfile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }

        ProfileEntity profile = foundProfile.get();
        profile.setBalance(profile.getBalance() + amount);

        profileRepository.save(profile);
    }
}
