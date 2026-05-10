package dequelite.financemirror_backend.app.profile;

import dequelite.financemirror_backend.app.profile.dto.ProfileGraphDto;
import dequelite.financemirror_backend.app.profile.dto.ReportGraphDto;
import dequelite.financemirror_backend.app.profile.dto.SpendingGraphDto;
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
                .spendings(mapSpendings(profile.getSpendings()))
                .reports(mapReports(profile.getReports()))
                .build();

        return dto;
    }

    private static List<SpendingGraphDto> mapSpendings(List<SpendingEntity> spendings) {
        return spendings.stream()
                .map(ProfileMapper::toSpendingDto)
                .toList();
    }

    private static List<ReportGraphDto> mapReports(List<ReportEntity> reports) {
        return reports.stream()
                .map(ProfileMapper::toReportDto)
                .toList();
    }

    private static SpendingGraphDto toSpendingDto(SpendingEntity spending) {
        return SpendingGraphDto.builder()
                .id(spending.getId())
                .date(spending.getDate())
                .price(spending.getPrice())
                .category(spending.getCategory().name())
                .description(spending.getDescription())
                .build();
    }

    private static ReportGraphDto toReportDto(ReportEntity report) {
        return ReportGraphDto.builder()
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