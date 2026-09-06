package dequelite.financemirror_backend.infra.security.filters.AuthFilter;

import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.AuthProcessor;
import dequelite.financemirror_backend.infra.security.processors.AuthProcessor.TokenHeaderDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter {

    @Value("${jwt.secret.session}")
    private String accessTokenSecret;

    private final AuthProcessor authProcessor;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        log.info("AuthFilter shouldNotFilter: {}", path);

        return (path.startsWith("/auth") && !path.startsWith("/auth/session"))
                || path.equals("/auth/session/refresh-token")
                || path.startsWith("/profile/payment")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("=== FILTER START === PATH: {}", request.getServletPath());

        String authHeader = request.getHeader("Authorization");

        log.info("HEADER: {}", authHeader);

        try {

            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized\"}");
                return;
            }

//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                filterChain.doFilter(request, response);
//                return;
//            }

            String token = authHeader.substring(7);

            log.info("TOKEN: {}", token);

            TokenHeaderDTO user =
                    authProcessor.validateToken(
                            token,
                            accessTokenSecret
                    );

            log.info("USER: {}", user);

            UsernamePasswordAuthenticationToken authentication =
                    UsernamePasswordAuthenticationToken.authenticated(
                            user,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );

            log.info("AUTH CREATED");

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            log.info(
                    "SECURITY CONTEXT: {}",
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            );

        } catch (Exception e) {
            log.error("FILTER ERROR", e);
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return;
        }

        log.info("=== FILTER END ===");

        filterChain.doFilter(request, response);
    }
}