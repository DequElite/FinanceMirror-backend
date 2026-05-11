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
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("=== FILTER START ===");

        String authHeader = request.getHeader("Authorization");

        log.info("HEADER: {}", authHeader);

        try {

            if(authHeader == null) {

                log.info("HEADER NULL");

                filterChain.doFilter(request, response);
                return;
            }

            if(!authHeader.startsWith("Bearer ")) {

                log.info("HEADER NOT BEARER");

                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);

            log.info("TOKEN: {}", token);

            TokenHeaderDTO user =
                    authProcessor.validateToken(
                            token,
                            accessTokenSecret
                    );

            log.info("USER: {}", user);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            List.of()
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
        }

        log.info("=== FILTER END ===");

        filterChain.doFilter(request, response);
    }
}