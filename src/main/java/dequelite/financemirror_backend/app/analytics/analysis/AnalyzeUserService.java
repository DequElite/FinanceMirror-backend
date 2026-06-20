package dequelite.financemirror_backend.app.analytics.analysis;

import dequelite.financemirror_backend.app.analytics.ai_report.dto.AnalyzedDto;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.profile.ProfileRepository;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentEntity;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentRepo;
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
    private final ReplenishmentRepo replenishmentRepo;

    public AnalyzedDto analyze(UUID userId){
        Optional<ProfileEntity> foundProfile = this.profileRepository.findByUser_Id(userId);
        if(foundProfile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        ProfileEntity profile = foundProfile.get();

        List<SpendingEntity> spendings = this.spendings(profile.getId());

        List<ReplenishmentEntity> balanceUpdates = this.balanceUpdates(profile.getId());

        long daysUntilEndOfMonth = this.daysUntilEndOfMonth();

        int totalSpent = this.totalSpent(spendings);

        int totalBalanceUpdates = this.totalBalanceUpdates(balanceUpdates);

        CategoryEnum expensiveCategory = this.expensiveCategory(spendings);

        int spentPrecentOfSalary = this.spentPercentOfSalary(profile.getMonthSalary(), totalSpent);

        int averageDailySpending = this.averageDailySpending(totalSpent);

        int dailyLimit = this.dailyLimit(daysUntilEndOfMonth, profile.getBalance());

        String financeResult = this.financeResult(totalBalanceUpdates, totalSpent);

        return AnalyzedDto.builder()
                .totalSpent(totalSpent)
                .expensiveCategory(expensiveCategory)
                .spentPercentOfSalary(spentPrecentOfSalary)
                .averageDailySpending(averageDailySpending)
                .dailyLimit(dailyLimit)
                .report_balance(profile.getBalance())
                .profile(foundProfile.get())
                .daysUntilSalary(this.daysUntilEndOfMonth())
                .financeResult(financeResult)
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

    private int totalBalanceUpdates (List<ReplenishmentEntity> balanceUpdates) {
        return balanceUpdates.stream()
                .mapToInt(ReplenishmentEntity::getAmount)
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

    private List<ReplenishmentEntity> balanceUpdates(UUID profileId) {
        LocalDate now = LocalDate.now();

        LocalDate startOfMonth = now.withDayOfMonth(1);

        return this.replenishmentRepo.findAllByProfile_IdAndDateBetween(
                profileId,
                startOfMonth,
                now
        );
    }

    private String financeResult(int totalBalanceUpdates, int totalSpent) {
         int result = totalBalanceUpdates - totalSpent;

         String financeResult;
         if(result < 0) {
             financeResult = "- " + String.valueOf(result);
         } else if (result > 0) {
             financeResult = "+ " + String.valueOf(result);
         } else {
             financeResult = "0";
         }

         return financeResult;
    }
}
