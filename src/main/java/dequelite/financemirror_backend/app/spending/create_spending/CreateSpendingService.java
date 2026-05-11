package dequelite.financemirror_backend.app.spending.create_spending;

import dequelite.financemirror_backend.app.spending.create_spending.dto.CreateSpendingRequestDto;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
import dequelite.financemirror_backend.domain.spending.CategoryEnum;
import dequelite.financemirror_backend.domain.spending.SpendingEntity;
import dequelite.financemirror_backend.domain.spending.SpendingRepository;
import dequelite.financemirror_backend.domain.user.UserRepository;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateSpendingService {
    private final ProfileRepository profileRepository;
    private final SpendingRepository spendingRepository;

    public void createSpending(
            CreateSpendingRequestDto body,
            TokenHeaderDTO user
    ) {
        Optional<ProfileEntity> profile = this.profileRepository.findByUser_Id(UUID.fromString(user.getSub()));
        if(profile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

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

        this.spendingRepository.save(newSpending);
     }
}
