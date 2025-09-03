package co.com.bancolombia.api.auth.filter;

import co.com.bancolombia.api.auth.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class JwtTokenValidatorFilter implements WebFilter {

    private final JwtUtils jwtUtils;

    public JwtTokenValidatorFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                DecodedJWT decodedJWT = jwtUtils.validateToken(token);

                String email = jwtUtils.extractEmail(decodedJWT);
                String authoritiesString = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();
                String normalizedAuthorities = Arrays.stream(authoritiesString.split(","))
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role.trim())
                        .collect(Collectors.joining(","));

                var authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(normalizedAuthorities);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContext context = new SecurityContextImpl(authToken);

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
            } catch (Exception e) {
                return chain.filter(exchange);
            }
        }

        return chain.filter(exchange);
    }
}