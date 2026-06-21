package dequelite.financemirror_backend.app.profile.balance.update_balance;

import dequelite.financemirror_backend.app.analytics.analysis.AnalyzeUserService;
import dequelite.financemirror_backend.app.profile.balance.update_balance.dto.UpdateBalanceRequestDto;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentEntity;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateBalanceService {
    private final ProfileRepository profileRepository;
    private final ReplenishmentRepo replenishmentRepo;
    private final AnalyzeUserService analyzeUserService;

    @Transactional
    public void updateBalance(UpdateBalanceRequestDto body, UUID userId) {
        Optional<ProfileEntity> foundProfile = this.profileRepository.findByUser_Id(userId);
        if (foundProfile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }

        ProfileEntity profile = foundProfile.get();
        profile.setBalance(profile.getBalance() + body.getAmount());

        ReplenishmentEntity newReplenishment = ReplenishmentEntity.builder()
                .profile(profile)
                .date(body.getDate())
                .amount(body.getAmount())
                .description(body.getDescription())
                .category(body.getType())
                .build();

        replenishmentRepo.save(newReplenishment);
        profileRepository.save(profile);

        analyzeUserService.analyze(userId);
    }
}
