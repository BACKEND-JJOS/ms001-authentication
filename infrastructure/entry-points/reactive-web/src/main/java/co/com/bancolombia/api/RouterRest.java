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

    public static final String BASE_PATH_USER = "/v1/user";
    private static final String BASE_PATH_AUTH_ROUTE = "/v1/auth/login";

    @Bean
    public WebProperties.Resources  resources(){return new WebProperties.Resources();}


    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route()
                .POST(BASE_PATH_AUTH_ROUTE,
                        handler::listenPOSTLogin,
                        OpenApiDoc::createUser)
                .POST(BASE_PATH_USER,
                        handler::listenPOSTCreateUserUseCase,
                        OpenApiDoc::createUser)
                .GET(BASE_PATH_USER+"/{identification}",
                        handler::listenGETFilteredUserByIdentificationUseCase,
                        OpenApiDoc::getUserByIdentification)
                .build();
    }
}
