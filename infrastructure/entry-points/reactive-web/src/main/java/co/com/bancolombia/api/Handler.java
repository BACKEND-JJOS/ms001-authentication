package co.com.bancolombia.api;

import co.com.bancolombia.api.mapper.UserRequestMapper;
import co.com.bancolombia.api.request.UserRequest;
import co.com.bancolombia.api.response.ApiResponse;
import co.com.bancolombia.api.validator.GenericValidator;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.saveuser.SaveUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private  final SaveUserUseCase saveUserUseCase;
    private static final String RESPONSE_OK = "Response Ok";


    public Mono<ServerResponse> listenPOSTCreateUserUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequest.class)
                        .flatMap(GenericValidator::validate)
                        .map(UserRequestMapper::toDomain)
                        .flatMap(user -> saveUserUseCase.execute(user)
                        .flatMap(savedUser -> buildResponse(savedUser, HttpStatus.CREATED.value(), RESPONSE_OK)))

         ;
    }

    private <T> Mono<ServerResponse> buildResponse(T data, int status, String message) {
        return ServerResponse.status(status).bodyValue(
                ApiResponse.<T>builder()
                        .data(data)
                        .status(status)
                        .message(message)
                        .build()
        );
    }
}
