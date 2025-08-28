package co.com.bancolombia.api;

import co.com.bancolombia.api.openapidoc.OpenApiDoc;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

@Configuration
public class RouterRest {

    @Bean
    public WebProperties.Resources  resources(){return new WebProperties.Resources();}


    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route()
                .POST("/v1/user",
                        handler::listenPOSTCreateUserUseCase,
                        OpenApiDoc::createService)
                .GET("/v1/user/{identification}",
                        handler::listenGETFilteredUserByIdentificationUseCase,
                        OpenApiDoc::createService) //TODO: Ajustar api doc
                .build();
    }
}
