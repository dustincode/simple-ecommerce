package com.dustincode.ecommerce.core.configs;

import com.dustincode.ecommerce.core.filters.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import static com.dustincode.ecommerce.core.constant.CommonConstants.REFRESH_TOKEN_URL;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Import(SecurityProblemSupport.class)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CorsFilter corsFilter;
    private final AuthenticationFilter authenticationFilter;
    private final SecurityProblemSupport problemSupport;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/**"))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(problemSupport)
                        .accessDeniedHandler(problemSupport)
                )
                .authorizeHttpRequests(request -> request
                        .requestMatchers(new AntPathRequestMatcher("swagger-ui/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("v3/api-docs/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()

                        .requestMatchers(new AntPathRequestMatcher("/static/**")).permitAll()

                        .requestMatchers(new AntPathRequestMatcher("/api/v1/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/register")).permitAll()

                        .requestMatchers(new AntPathRequestMatcher(REFRESH_TOKEN_URL)).permitAll()

                        .requestMatchers(new AntPathRequestMatcher("/api/v1/reset-password")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/reset-password/**")).permitAll()

                        .requestMatchers(new AntPathRequestMatcher("/api/common/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
