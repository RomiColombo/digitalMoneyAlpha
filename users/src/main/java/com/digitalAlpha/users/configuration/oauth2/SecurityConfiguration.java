package com.digitalAlpha.users.configuration.oauth2;


import com.digitalAlpha.users.configuration.keycloak.KeycloakGrantedAuthoritiesConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakGrantedAuthoritiesConverter());
        http.cors().disable().csrf().disable()
                .authorizeExchange().pathMatchers(HttpMethod.POST,"/users").permitAll()
                .and()
                .authorizeExchange().anyExchange().authenticated()
                .and()
                .oauth2ResourceServer().jwt().jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(converter));
        return http.build();
    }
}
