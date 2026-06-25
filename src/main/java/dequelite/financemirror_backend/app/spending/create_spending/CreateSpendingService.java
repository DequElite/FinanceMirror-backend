package dequelite.financemirror_backend.app.spending.create_spending;

import dequelite.financemirror_backend.app.analytics.analysis.AnalyzeUserService;
import dequelite.financemirror_backend.app.analytics.auto_report.AutoReportService;
import dequelite.financemirror_backend.app.spending.create_spending.dto.CreateSpendingRequestDto;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
import dequelite.financemirror_backend.domain.spending.CategoryEnum;
import dequelite.financemirror_backend.domain.spending.SpendingEntity;
import dequelite.financemirror_backend.domain.spending.SpendingRepository;
import dequelite.financemirror_backend.domain.user.UserRepository;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateSpendingService {
    private final ProfileRepository profileRepository;
    private final SpendingRepository spendingRepository;
    private final AutoReportService autoReportService;

    @Transactional
    public void createSpending(
            CreateSpendingRequestDto body,
            TokenHeaderDTO user
    ) {
        log.info("SERVICE createSpending --START: {}", user);
        Optional<ProfileEntity> profile = this.profileRepository.findByUser_Id(UUID.fromString(user.getSub()));
        if(profile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        profile.get().setBalance(profile.get().getBalance() - body.getPrice());

        CategoryEnum category;

        try {
            category = CategoryEnum.valueOf(body.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid category"
            );
        }

        SpendingEntity newSpending = SpendingEntity.builder()
                .profile(profile.get())
                .date(body.getDate())
                .price(body.getPrice())
                .description(body.getDescription())
                .category(category)
                .build();

        try {
            this.spendingRepository.save(newSpending);
        } catch (Exception e) {
            log.error("SAVE ERROR", e);
            throw e;
        }

        autoReportService.createAutoReport(UUID.fromString(user.getSub()));

        log.info("SERVICE createSpending --end: {}", newSpending);
     }
}
