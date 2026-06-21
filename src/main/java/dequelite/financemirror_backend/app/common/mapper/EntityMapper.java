package dequelite.financemirror_backend.app.common.mapper;

import dequelite.financemirror_backend.domain.dto.*;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentEntity;
import dequelite.financemirror_backend.domain.report.ReportEntity;
import dequelite.financemirror_backend.domain.spending.SpendingEntity;
import dequelite.financemirror_backend.domain.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public static ReplenishmentDto toReplenishmentDto(ReplenishmentEntity replenishment) {
        if(replenishment == null) {
            return null;
        }

        return ReplenishmentDto.builder()
                .id(replenishment.getId())
                .date(replenishment.getDate())
                .amount(replenishment.getAmount())
                .description(replenishment.getDescription())
                .category(replenishment.getCategory())
                .build();
    }

    public static UserDto toUserDto(UserEntity user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .profile(toProfileDto(user.getProfile()))
                .build();
    }

    public static ProfileDto toProfileDto(ProfileEntity profile) {
        if (profile == null) {
            return null;
        }

        return ProfileDto.builder()
                .id(profile.getId())
                .balance(profile.getBalance())
                .monthSalary(profile.getMonthSalary())
                .currency(profile.getCurrency())
                .build();
    }

    public static SpendingDto toSpendingDto(SpendingEntity spending) {
        if (spending == null) {
            return null;
        }

        return SpendingDto.builder()
                .id(spending.getId())
                .price(spending.getPrice())
                .description(spending.getDescription())
                .category(spending.getCategory())
                .date(spending.getDate())
                .build();
    }

    public static ReportDto toReportDto(ReportEntity report) {
        if (report == null) {
            return null;
        }

        return ReportDto.builder()
                .id(report.getId())
                .weeklySpending(report.getWeeklySpending())
                .spentPercentOfSalary(report.getSpentPercentOfSalary())
                .averageDailySpending(report.getAverageDailySpending())
                .dailyLimit(report.getDailyLimit())
                .reportBalance(report.getReportBalance())
                .expensiveCategory(report.getExpensive_category().name())
                .financeResult(report.getFinanceResult())
                .expenditureRate(report.getExpenditureRate())
                .leakProblem(report.getLeakProblem())
                .advice(report.getAdvice())
                .build();
    }
}