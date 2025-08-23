package co.com.bancolombia.api;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.saveuser.SaveUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private  final SaveUserUseCase saveUserUseCase;

    User user = new User(
            null,
            "John",
            "Doe",
            "john.doe@test.com",
            "123456789",
            "555-1234",
            2,
            1000.0
    );

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        return saveUserUseCase.execute(user)
                .flatMap(savedUser -> ServerResponse.ok().bodyValue(savedUser))
                .switchIfEmpty(ServerResponse.badRequest().build());
    }
}
