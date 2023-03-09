package com.digitalAlpha.middleware.configuration.feign;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;

import static java.util.Objects.isNull;


public class OAuthClientCredentialFeignManager {
    private final ReactiveOAuth2AuthorizedClientManager manager;
    private final Authentication principal;
    private final ClientRegistration clientRegistration;

    public OAuthClientCredentialFeignManager(ReactiveOAuth2AuthorizedClientManager manager, ClientRegistration clientRegistration) {
        this.manager = manager;
        this.principal = createPrincipal();
        this.clientRegistration = clientRegistration;
    }

    private Authentication createPrincipal() {
        return new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.emptySet();
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return this;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }

            @Override
            public String getName() {
                return clientRegistration.getClientId();
            }
        };
    }

    public Mono<String> getAccessToken() {
            OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId(clientRegistration.getRegistrationId())
                    .principal(principal)
                    .build();
            Mono<OAuth2AuthorizedClient> client = manager.authorize(oAuth2AuthorizeRequest);
            client.doOnNext(oAuth2AuthorizedClient -> {
                if (isNull(oAuth2AuthorizedClient)) {
                    throw new IllegalStateException("client credentials flow on " + clientRegistration.getRegistrationId() + " failed, client is null");
                }
            });
            return client.map(c -> c.getAccessToken().getTokenValue());
    }

}
