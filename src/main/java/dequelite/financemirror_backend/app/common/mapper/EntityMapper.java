package dequelite.financemirror_backend.app.common.mapper;

import dequelite.financemirror_backend.domain.dto.*;
import dequelite.financemirror_backend.domain.history_report.HistoryReportEntity;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.replenishment.ReplenishmentEntity;
import dequelite.financemirror_backend.domain.report.ReportEntity;
import dequelite.financemirror_backend.domain.session.SessionEntity;
import dequelite.financemirror_backend.domain.spending.SpendingEntity;
import dequelite.financemirror_backend.domain.subscription.SubscriptionEntity;
import dequelite.financemirror_backend.domain.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityMapper {

    public static List<SessionDto> mapSessionDto(List<SessionEntity> sessionEntities) {
        return sessionEntities
                .stream().map(EntityMapper::toSessionDto).toList();
    }

    public static SessionDto toSessionDto(SessionEntity session) {
        if(session == null) {
            return null;
        }

        return SessionDto.builder()
                .id(session.getId().toString())
                .expiresAt(session.getExpiresAt())
                .createdAt(session.getCreatedAt())
                .ipAddress(session.getIpAddress())
                .userAgent(session.getUserAgent())
                .build();
    }

    public static SubscriptionDto toSubscriptionDto(SubscriptionEntity subscription) {
        if (subscription == null) {
            return null;
        }

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

    public static ReplenishmentDto toReplenishmentDto(ReplenishmentEntity replenishment) {
        if(replenishment == null) {
            return null;
        }

        return ReplenishmentDto.builder()
                .id(replenishment.getId())
                .date(replenishment.getDate())
                .amount(replenishment.getAmount())
                .description(replenishment.getDescription())
                .type(replenishment.getType())
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
                .type(report.getType())
                .totalSpent(report.getTotalSpent())
                .spentPercentOfSalary(report.getSpentPercentOfSalary())
                .averageDailySpending(report.getAverageDailySpending())
                .dailyLimit(report.getDailyLimit())
                .reportBalance(report.getReportBalance())
                .expensiveCategory(report.getExpensive_category().name())
                .financeResult(report.getFinanceResult())
                .expenditureRate(report.getExpenditureRate())
                .leakProblem(report.getLeakProblem())
                .advice(report.getAdvice())
                .created_at(report.getCreatedAt())
                .build();
    }

    public static HistoryReportDto toHistoryReportDto(HistoryReportEntity report) {
        if (report == null) {
            return null;
        }

        return HistoryReportDto.builder()
                .id(report.getId())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .periodStartDate(report.getPeriodStartDate())
                .periodEndDate(report.getPeriodEndDate())
                .totalSpent(report.getTotalSpent())
                .currentBalance(report.getCurrentBalance())
                .totalReplenishments(report.getTotalReplenishments())
                .averageDailySpending(report.getAverageDailySpending())
                .spentDifference(report.getSpentDifference())
                .spentDifferencePercent(report.getSpentDifferencePercent())
                .replenishedDifference(report.getReplenishedDifference())
                .replenishedDifferencePercent(report.getReplenishedDifferencePercent())
                .balanceDifference(report.getBalanceDifference())
                .balanceDifferencePercent(report.getBalanceDifferencePercent())
                .averageDailySpendingDifference(report.getAverageDailySpendingDifference())
                .averageDailySpendingDifferencePercent(report.getAverageDailySpendingDifferencePercent())
                .build();
}