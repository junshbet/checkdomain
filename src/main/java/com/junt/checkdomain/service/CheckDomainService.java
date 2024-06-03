package com.junt.checkdomain.service;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import javax.net.ssl.SSLException;
import java.util.HashMap;
import java.util.Map;
@Service
public class CheckDomainService {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public CheckDomainService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public WebClient createInsecureWebClient(String proxyHost, int proxyPort, String proxyUsername, String proxyPassword) {
        HttpClient httpClient = HttpClient.create()
                .secure(sslContextSpec -> {
                    try {
                        SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
                        sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                        sslContextSpec.sslContext(sslContextBuilder.build());
                    } catch (SSLException e) {
                        e.printStackTrace();
                    }
                })
                .proxy(proxy -> {
                    ProxyProvider.Builder proxyBuilder = proxy.type(ProxyProvider.Proxy.HTTP).host(proxyHost).port(proxyPort);
                    if (proxyUsername != null && !proxyUsername.isEmpty() && proxyPassword != null && !proxyPassword.isEmpty()) {
                        proxyBuilder.username(proxyUsername).password(p -> proxyPassword);
                    }
                });

        return webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public Mono<Map<String, Object>> getStatusCodeFromExternalApi(String url, String proxyHost, int proxyPort, String proxyUsername, String proxyPassword) {
        WebClient webClient = createInsecureWebClient(proxyHost, proxyPort, proxyUsername, proxyPassword);
        return webClient.get()
                .uri(url)
                .exchangeToMono(response -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("status", response.statusCode().value());
                    return Mono.just(result);
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("status", ex.getRawStatusCode());
                    errorResult.put("error", ex.getMessage());
                    return Mono.just(errorResult);
                })
                .onErrorResume(Exception.class, ex -> {
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                    errorResult.put("error", ex.getMessage());
                    return Mono.just(errorResult);
                });
    }
}


