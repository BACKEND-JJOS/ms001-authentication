package co.com.bancolombia.api;

import co.com.bancolombia.api.mapper.UserRequestMapper;
import co.com.bancolombia.api.request.UserRequest;
import co.com.bancolombia.api.response.ApiResponse;
import co.com.bancolombia.api.validator.GenericValidator;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.saveuser.SaveUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private  final SaveUserUseCase saveUserUseCase;
    private static final String RESPONSE_OK = "Response Ok";


    public Mono<ServerResponse> listenPOSTCreateUserUseCase(ServerRequest serverRequest) {
        log.debug("MESSAGE_HANDLER_LOG_TRACE : INIT METHOD USER CREATE");
        return serverRequest.bodyToMono(UserRequest.class)
                .flatMap(GenericValidator::validate)
                .map(UserRequestMapper::toDomain)
                .flatMap(user -> {
                    log.info("MESSAGE_HANDLER_LOG_TRACE : Received request to create user with email={}", user.getEmail());
                    return saveUserUseCase.execute(user)
                            .doOnSuccess(u -> log.info("MESSAGE_HANDLER_LOG_TRACE : Successfully created user with id={}", u.getIdUser()))
                            .doOnError(err -> log.error("MESSAGE_HANDLER_LOG_TRACE : Error while creating user with email={} - {}", user.getEmail(), err.getMessage()))
                            .flatMap(savedUser -> buildResponse(savedUser, HttpStatus.CREATED.value(), RESPONSE_OK));
                });
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
