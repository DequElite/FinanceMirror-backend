package dequelite.financemirror_backend.app.analytics.dashboard;

import dequelite.financemirror_backend.app.analytics.dashboard.dto.DashboardGraphDto;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService service;

    @QueryMapping
    public DashboardGraphDto dashboard(
            @AuthenticationPrincipal TokenHeaderDTO user
            ){
        return this.service.dashboard(UUID.fromString(user.getSub()));
    }
}
