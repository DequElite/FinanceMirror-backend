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

        return ProfileGraphDto.builder()
                .id(profile.getId())
                .email(profile.getUser().getEmail())
                .balance(profile.getBalance())
                .monthSalary(profile.getMonthSalary())
                .currency(profile.getCurrency())
                .spendings(mapSpendings(profile.getSpendings()))
                .replenishment(mapReplenishments(profile.getReplenishments()))
                .subscription(EntityMapper.toSubscriptionDto(profile.getUser().getSubscription()))
                .build();
    }

    private static List<ReplenishmentDto> mapReplenishments(List<ReplenishmentEntity> replenishments) {
        return replenishments.stream()
                .map(EntityMapper::toReplenishmentDto)
                .toList();
    }

    private static List<SpendingDto> mapSpendings(List<SpendingEntity> spendings) {
        return spendings.stream()
                .map(EntityMapper::toSpendingDto)
                .toList();
    }
}