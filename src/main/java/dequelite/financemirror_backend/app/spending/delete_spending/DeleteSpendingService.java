package dequelite.financemirror_backend.app.spending.delete_spending;

import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
import dequelite.financemirror_backend.domain.spending.SpendingEntity;
import dequelite.financemirror_backend.domain.spending.SpendingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteSpendingService {
    private final ProfileRepository profileRepository;
    private final SpendingRepository spendingRepository;

    public void deleteSpending(
            UUID userId,
            UUID spendingId
    ) {
        Optional<ProfileEntity> profile = this.profileRepository.findByUser_Id(userId);
        if(profile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Optional<SpendingEntity> spending = this.spendingRepository.findByIdAndProfile_Id(
                spendingId,
                profile.get().getId()
        );
        if(spending.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        this.spendingRepository.delete(spending.get());
    }
}
