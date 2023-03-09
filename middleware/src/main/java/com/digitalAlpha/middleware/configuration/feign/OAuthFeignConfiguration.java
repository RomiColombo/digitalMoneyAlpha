package com.digitalAlpha.middleware.configuration.feign;

import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.form.spring.SpringFormEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Configuration
@RequiredArgsConstructor
public class OAuthFeignConfiguration {

    public static final String CLIENT_REGISTRATION_ID = "keycloak";
    private final ReactiveOAuth2AuthorizedClientService clientService;
    private final ReactiveClientRegistrationRepository repository;
    private final ObjectFactory<HttpMessageConverters> messageConverters;


    @Bean
    public ReactiveHttpRequestInterceptor requestInterceptor() {
        return reactiveHttpRequest -> repository.findByRegistrationId(CLIENT_REGISTRATION_ID)
                .map(client -> new OAuthClientCredentialFeignManager(authorizedClientManager(), client))
                .zipWith(Mono.just(reactiveHttpRequest))
                .flatMap(t -> t.getT1().getAccessToken().map(str -> {
                    String token = "Bearer " + str;
                    t.getT2().headers().put("Authorization", Collections.singletonList(token));
                    return t.getT2();
                }));
    }

    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager() {
        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials().build();
        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager clientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(repository,clientService);
        clientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return clientManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }

    @Bean
    @Primary
    @Scope(SCOPE_PROTOTYPE)
    Encoder feignFormEncoder() {
        return new FormEncoder(new SpringEncoder(this.messageConverters));
    }

}