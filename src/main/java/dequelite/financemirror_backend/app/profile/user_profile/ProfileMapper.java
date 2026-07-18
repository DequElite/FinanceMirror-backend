package dequelite.financemirror_backend.app.profile.user_profile;

import dequelite.financemirror_backend.app.common.mapper.EntityMapper;
import dequelite.financemirror_backend.app.profile.user_profile.dto.ProfileGraphDto;
import dequelite.financemirror_backend.domain.dto.ReplenishmentDto;
import dequelite.financemirror_backend.domain.dto.ReportDto;
import dequelite.financemirror_backend.domain.dto.SpendingDto;
import dequelite.financemirror_backend.domain.dto.SubscriptionDto;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentEntity;
import dequelite.financemirror_backend.domain.report.ReportEntity;
import dequelite.financemirror_backend.domain.spending.SpendingEntity;
import dequelite.financemirror_backend.domain.subscription.SubscriptionEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfileMapper {

    public ProfileGraphDto toDto(ProfileEntity profile) {
        ProfileGraphDto dto = ProfileGraphDto.builder()
                .id(profile.getId())
                .email(profile.getUser().getEmail())
                .balance(profile.getBalance())
                .monthSalary(profile.getMonthSalary())
                .currency(profile.getCurrency())
                .spendings(mapSpendings(profile.getSpendings()))
                .reports(mapReports(profile.getReports()))
                .replenishment(mapReplenishments(profile.getReplenishments()))
                .subscription(toSubscriptionDto(profile.getUser().getSubscription()))
                .build();

        return dto;
    }

    private static List<ReplenishmentDto> mapReplenishments(List<ReplenishmentEntity> replenishments) {
        return replenishments.stream()
                .map(ProfileMapper::toReplenishmentDto)
                .toList();
    }

    private static List<SpendingDto> mapSpendings(List<SpendingEntity> spendings) {
        return spendings.stream()
                .map(ProfileMapper::toSpendingDto)
                .toList();
    }

    private static List<ReportDto> mapReports(List<ReportEntity> reports) {
        return reports.stream()
                .map(ProfileMapper::toReportDto)
                .toList();
    }

    private static SubscriptionDto toSubscriptionDto(SubscriptionEntity subscription) {
        return SubscriptionDto.builder()
                .id(subscription.getId())
                .type(subscription.getType())
                .status(subscription.getStatus())
                .created_at(subscription.getCreated_at())
                .started_at(subscription.getStarted_at())
                .updated_at(subscription.getUpdated_at())
                .expires_at(subscription.getExpires_at())
                .build();
    }

    private static SpendingDto toSpendingDto(SpendingEntity spending) {
        return SpendingDto.builder()
                .id(spending.getId())
                .date(spending.getDate())
                .price(spending.getPrice())
                .category(spending.getCategory())
                .description(spending.getDescription())
                .build();
    }

    private static ReportDto toReportDto(ReportEntity report) {
        return ReportDto.builder()
                .id(report.getId())
                .type(report.getType())
                .weeklySpending(report.getWeeklySpending())
                .spentPercentOfSalary(report.getSpentPercentOfSalary())
                .averageDailySpending(report.getAverageDailySpending())
                .dailyLimit(report.getDailyLimit())
                .reportBalance(report.getReportBalance())
                .expensiveCategory(report.getExpensive_category().name())
                .expenditureRate(report.getExpenditureRate())
                .leakProblem(report.getLeakProblem())
                .advice(report.getAdvice())
                .financeResult(report.getFinanceResult())
                .build();
    }

    private static ReplenishmentDto toReplenishmentDto(ReplenishmentEntity replenishment) {
        return ReplenishmentDto.builder()
                .id(replenishment.getId())
                .date(replenishment.getDate())
                .amount(replenishment.getAmount())
                .description(replenishment.getDescription())
                .type(replenishment.getType())
                .build();
    }
}