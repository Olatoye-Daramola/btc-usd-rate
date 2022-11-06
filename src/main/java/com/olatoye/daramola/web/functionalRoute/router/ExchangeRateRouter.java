package com.olatoye.daramola.web.functionalRoute.router;

import com.olatoye.daramola.web.functionalRoute.handler.ExchangeRateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.servlet.function.RequestPredicates.accept;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
public class ExchangeRateRouter {

    private final ExchangeRateHandler erHandler;

    public ExchangeRateRouter(ExchangeRateHandler erHandler) {
        this.erHandler = erHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return
                route().path("/rates", builder -> builder
                        .nest(accept(APPLICATION_JSON), routeBuilder -> routeBuilder
                                .GET("/", erHandler::getLatestRate)
                                .GET("/period", erHandler::getRateBetweenDate)
                        )
                ).build();
    }
}
