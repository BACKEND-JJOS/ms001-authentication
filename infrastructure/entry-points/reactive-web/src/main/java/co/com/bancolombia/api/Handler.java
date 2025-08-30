package co.com.bancolombia.api;

import co.com.bancolombia.api.mapper.UserRequestMapper;
import co.com.bancolombia.api.request.UserRequest;
import co.com.bancolombia.api.response.ApiResponse;
import co.com.bancolombia.api.validator.GenericValidator;
import co.com.bancolombia.model.responsecode.ResponseCode;
import co.com.bancolombia.usecase.filteruserbyidentification.FilterUserByIdentificationUseCase;
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

    private final SaveUserUseCase saveUserUseCase;
    private final FilterUserByIdentificationUseCase filterUserByIdentificationUseCase;


    public Mono<ServerResponse> listenPOSTCreateUserUseCase(ServerRequest serverRequest) {
        log.info("MESSAGE_HANDLER_LOG_TRACE : INIT METHOD USER CREATE");
        return serverRequest.bodyToMono(UserRequest.class)
                .flatMap(GenericValidator::validate)
                .flatMap(userRequest -> {
                    var user =  UserRequestMapper.toDomain(userRequest);
                    log.info("MESSAGE_HANDLER_LOG_TRACE : Received request to create user with email={}", user.getEmail());
                    return saveUserUseCase.execute(user)
                            .doOnSuccess(u -> log.info("MESSAGE_HANDLER_LOG_TRACE : Successfully created user with id={}", u.getIdUser()))
                            .doOnError(err -> log.error("MESSAGE_HANDLER_LOG_TRACE : Error while creating user with email={} - {}", user.getEmail(), err.getMessage()))
                            .flatMap(savedUser -> buildResponse(savedUser, HttpStatus.CREATED.value(), ResponseCode.USER_CREATED_SUCCESSFULLY));
                });
    }

    public Mono<ServerResponse> listenGETFilteredUserByIdentificationUseCase(ServerRequest serverRequest){
        String identification = serverRequest.pathVariable("identification");
        log.info("MESSAGE_HANDLER_LOG_TRACE : INIT METHOD FILTER USER BY IDENTIFICATION");

        return filterUserByIdentificationUseCase.execute(identification)
                .doOnSuccess(user -> log.info("MESSAGE_HANDLER_LOG_TRACE : User found with identification={}", identification))
                .doOnError(err -> log.error("MESSAGE_HANDLER_LOG_TRACE : Error searching user with identification={} - {}", identification, err.getMessage()))
                .flatMap(user -> buildResponse(user, HttpStatus.OK.value(), ResponseCode.USER_FILTERED_SUCCESSFULLY))
                .switchIfEmpty(buildResponse(null, HttpStatus.NOT_FOUND.value(), ResponseCode.USER_NOT_EXISTS));
    }


    private <T> Mono<ServerResponse> buildResponse(T data, int status, String message) {
        return ServerResponse.status(status).bodyValue(
                ApiResponse.<T>builder()
                        .data(data)
                        .code(message)
                        .build()
        );
    }
}
