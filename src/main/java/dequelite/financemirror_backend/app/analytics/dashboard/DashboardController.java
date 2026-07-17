package dequelite.financemirror_backend.app.analytics.dashboard;

import dequelite.financemirror_backend.app.analytics.dashboard.dto.DashboardGraphDto;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {
    private final DashboardService service;

    @QueryMapping
    public DashboardGraphDto dashboard(
            @AuthenticationPrincipal TokenHeaderDTO user
            ){
        DashboardGraphDto response = this.service.dashboard(UUID.fromString(user.getSub()));
        log.info("dashboard: {}", response);
        return response;
    }
}
