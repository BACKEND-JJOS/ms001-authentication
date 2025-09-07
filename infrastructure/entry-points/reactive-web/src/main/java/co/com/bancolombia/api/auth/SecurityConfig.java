package co.com.bancolombia.api.auth;

import co.com.bancolombia.api.auth.filter.JwtTokenValidatorFilter;
import co.com.bancolombia.api.exceptions.ForbiddenUnAuthorizedException;
import co.com.bancolombia.exceptions.BusinessUnAuthorizedException;
import co.com.bancolombia.responsecode.ResponseCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtTokenValidatorFilter jwtTokenValidatorFilter;

    public SecurityConfig(JwtTokenValidatorFilter jwtTokenValidatorFilter) {
        this.jwtTokenValidatorFilter = jwtTokenValidatorFilter;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
                        .pathMatchers("/doc/swagger-ui/**", "/doc/api-docs/**", "/v3/api-docs/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/v1/user/**").hasAnyRole("ADMIN", "ASESOR")
                        .pathMatchers(HttpMethod.GET, "/v1/user/**").hasAnyRole("CLIENTE")
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtTokenValidatorFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, ex) ->
                                Mono.error(new BusinessUnAuthorizedException(ResponseCode.UNAUTHORIZED))
                        )
                        .accessDeniedHandler((exchange, denied) ->
                                Mono.error(new ForbiddenUnAuthorizedException(ResponseCode.FORBIDDEN))
                        )
                );
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}