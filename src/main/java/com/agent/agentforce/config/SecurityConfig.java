package com.agent.agentforce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("frame-ancestors 'self' https://*.salesforce.com https://*.force.com https://*.my.site.com")
                )
            )
            .csrf(csrf -> csrf.disable()); // 필요시 csrf 비활성화

        return http.build();
    }
}
