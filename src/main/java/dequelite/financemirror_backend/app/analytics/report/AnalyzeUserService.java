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

        ProfileEntity profile = foundProfile.get();

        List<SpendingEntity> spendings = this.spendings(profile.getId());

        long daysUntilEndOfMonth = this.daysUntilEndOfMonth();

        int totalSpent = this.totalSpent(spendings);

        CategoryEnum expensiveCategory = this.expensiveCategory(spendings);

        int spentPrecentOfSalary = this.spentPercentOfSalary(profile.getMonthSalary(), totalSpent);

        int averageDailySpending = this.averageDailySpending(totalSpent);

        int dailyLimit = this.dailyLimit(daysUntilEndOfMonth, profile.getBalance());

        return AnalyzedDto.builder()
                .totalSpent(totalSpent)
                .expensiveCategory(expensiveCategory)
                .spentPercentOfSalary(spentPrecentOfSalary)
                .averageDailySpending(averageDailySpending)
                .dailyLimit(dailyLimit)
                .report_balance(profile.getBalance())
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

    private int dailyLimit(long daysUntilEndOfMonth, int balance) {
        return (int) (balance / daysUntilEndOfMonth);
    }

    private int averageDailySpending(int spent) {
        int days = LocalDate.now().getDayOfMonth();
        if (days == 0) return spent;

        return spent / days;
    }

    private int spentPercentOfSalary(int salary, int spent) {
        if (salary == 0) return 0;

        return (int) ((spent * 100.0) / salary);
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

        LocalDate startOfMonth = now.withDayOfMonth(1);

        return this.spendingRepository.findAllByProfile_IdAndDateBetween(
                profileId,
                startOfMonth,
                now
        );
    }
}
