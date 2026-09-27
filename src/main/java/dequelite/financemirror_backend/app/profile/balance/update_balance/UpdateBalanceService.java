package dequelite.financemirror_backend.app.profile.balance.update_balance;

import dequelite.financemirror_backend.app.analytics.auto_report.AutoReportService;
import dequelite.financemirror_backend.app.common.response.ApiException;
import dequelite.financemirror_backend.app.common.response.ErrorCode;
import dequelite.financemirror_backend.app.profile.balance.update_balance.dto.UpdateBalanceRequestDto;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentEntity;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateBalanceService {
    private final ProfileRepository profileRepository;
    private final ReplenishmentRepo replenishmentRepo;
    private final AutoReportService autoReportService;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public void updateBalance(UpdateBalanceRequestDto body, UUID userId) {
        Optional<ProfileEntity> foundProfile = this.profileRepository.findByUser_Id(userId);
        if (foundProfile.isEmpty()) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        ProfileEntity profile = foundProfile.get();

        profileRepository.incrementBalance(profile.getId(), body.getAmount());

        entityManager.refresh(profile);

        ReplenishmentEntity newReplenishment = ReplenishmentEntity.builder()
                .profile(profile)
                .date(body.getDate())
                .amount(body.getAmount())
                .description(body.getDescription())
                .type(body.getType())
                .build();

        replenishmentRepo.save(newReplenishment);

        autoReportService.createAutoReport(userId);
    }
}
