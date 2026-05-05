package fr.dssi.phishingawareness.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class LocalDevAuthenticationFilter extends OncePerRequestFilter {

    private final Environment environment;
    private final boolean enabledByProperty;
    private final String actorId;
    private final List<SimpleGrantedAuthority> authorities;

    public LocalDevAuthenticationFilter(
            Environment environment,
            @Value("${app.local-dev-auth.enabled:false}") boolean enabledByProperty,
            @Value("${app.local-dev-auth.actor-id:00000000-0000-0000-0000-000000000101}") String actorId,
            @Value("${app.local-dev-auth.roles:ROLE_DSSI_ADMIN,ROLE_CAMPAIGN_MANAGER,ROLE_CAMPAIGN_VALIDATOR,ROLE_REPORT_VIEWER,ROLE_AUDITOR}") String roles) {
        this.environment = environment;
        this.enabledByProperty = enabledByProperty;
        this.actorId = actorId;
        this.authorities = Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isEnabled() && isAdminApi(request) && SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(actorId, "LOCAL_DEV_AUTH", authorities));
        }

        filterChain.doFilter(request, response);
    }

    private boolean isEnabled() {
        return enabledByProperty || Arrays.asList(environment.getActiveProfiles()).contains("local");
    }

    private boolean isAdminApi(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/admin/");
    }
}
