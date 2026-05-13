package dequelite.financemirror_backend.app.analytics.report;

import dequelite.financemirror_backend.app.analytics.report.dto.AnalyzedDto;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
import dequelite.financemirror_backend.domain.spending.CategoryEnum;
import dequelite.financemirror_backend.domain.spending.SpendingEntity;
import dequelite.financemirror_backend.domain.spending.SpendingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalyzeUserService {
    private final ProfileRepository profileRepository;
    private final SpendingRepository spendingRepository;

    public AnalyzedDto analyze(UUID userId){
        Optional<ProfileEntity> foundProfile = this.profileRepository.findByUser_Id(userId);
        if(foundProfile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<SpendingEntity> spendings = this.spendings(foundProfile.get().getId());

        int totalSpent = this.totalSpent(spendings);

        int remains = foundProfile.get().getMonthSalary() - totalSpent;

        return AnalyzedDto.builder()
                .totalSpent(totalSpent)
                .expensiveCategory(this.expensiveCategory(spendings))
                .remains(remains)
                .profile(foundProfile.get())
                .daysUntilSalary(this.daysUntilEndOfMonth())
                .build();
    }

    private long daysUntilEndOfMonth() {

        LocalDate now = LocalDate.now();

        LocalDate endOfMonth = now.withDayOfMonth(
                now.lengthOfMonth()
        );

        return ChronoUnit.DAYS.between(
                now,
                endOfMonth
        );
    }

    private CategoryEnum expensiveCategory (List<SpendingEntity> spendings) {
        Map<CategoryEnum, Integer> categoryTotals = new HashMap<>();

        for(SpendingEntity spending : spendings) {
            CategoryEnum category = spending.getCategory();

            if(categoryTotals.containsKey(category)) {

                categoryTotals.compute(
                        category,
                        (k, currentTotal) -> currentTotal + spending.getPrice()
                );

            } else {
                categoryTotals.put(category, spending.getPrice());
            }
        }

        CategoryEnum mostExpensiveCategory = null;
        int maxSpent = 0;

        for (Map.Entry<CategoryEnum, Integer> entry : categoryTotals.entrySet()) {
            if(entry.getValue() > maxSpent) {
                maxSpent = entry.getValue();
                mostExpensiveCategory = entry.getKey();
            }
        }

        return mostExpensiveCategory;
    }

    private int totalSpent (List<SpendingEntity> spendings) {
        return spendings.stream()
                .mapToInt(SpendingEntity::getPrice)
                .sum();
    }

    private List<SpendingEntity> spendings(UUID profileId){
        LocalDate now = LocalDate.now();

        LocalDate startOfWeek = now.minusDays(7);

        return this.spendingRepository.findAllByProfile_IdAndDateBetween(
                profileId,
                startOfWeek,
                now
        );
    }
}
