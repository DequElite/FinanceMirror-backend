package dequelite.financemirror_backend.infra.security.filters.AuthFilter;

import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.AuthProcessor;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
    @Value("${jwt.secret.session}")
    private String accessTokenSecret;

    private final AuthProcessor authProcessor;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String[] authHeaderPats = request.getHeader("auth").split(" ");

        TokenHeaderDTO tokenHeaderDTO = this.authProcessor.validateToken(authHeaderPats[1], accessTokenSecret);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(tokenHeaderDTO, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
