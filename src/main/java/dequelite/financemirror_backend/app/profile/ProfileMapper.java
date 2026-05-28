package dequelite.financemirror_backend.app.profile;

import dequelite.financemirror_backend.app.profile.dto.ProfileGraphDto;
import dequelite.financemirror_backend.domain.dto.ReportDto;
import dequelite.financemirror_backend.domain.dto.SpendingDto;
import dequelite.financemirror_backend.domain.profile.ProfileEntity;
import dequelite.financemirror_backend.domain.report.ReportEntity;
import dequelite.financemirror_backend.domain.spending.SpendingEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfileMapper {

    public ProfileGraphDto toDto(ProfileEntity profile) {
        ProfileGraphDto dto = ProfileGraphDto.builder()
                .id(profile.getId())
                .email(profile.getUser().getEmail())
                .monthSalary(profile.getMonthSalary())
                .currency(profile.getCurrency())
                .spendings(mapSpendings(profile.getSpendings()))
                .reports(mapReports(profile.getReports()))
                .build();

        return dto;
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
                .weeklySpending(report.getWeeklySpending())
                .expensiveCategory(report.getExpensive_category().name())
                .remains(report.getRemains())
                .expenditureRate(report.getExpenditureRate())
                .leakProblem(report.getLeakProblem())
                .advice(report.getAdvice())
                .build();
    }
}