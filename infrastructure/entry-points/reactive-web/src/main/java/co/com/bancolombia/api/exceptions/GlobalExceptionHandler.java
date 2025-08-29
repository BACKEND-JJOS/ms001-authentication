package co.com.bancolombia.api.exceptions;

import co.com.bancolombia.api.response.ApiResponse;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.exceptions.TechnicalException;
import co.com.bancolombia.model.responsecode.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageReaders(serverCodecConfigurer.getReaders());
        this.setMessageWriters(serverCodecConfigurer.getWriters());

    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::customErrorResponse);
    }

    private Mono<ServerResponse> customErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        log.error("MESSAGE_EXCEPTION_LOG_TRACE : Handling exception - {}", error.toString());

        HttpStatus status;
        String code = ResponseCode.TECHNICAL_ERROR;
        Object data = null;

        if (error instanceof BusinessException) {
            status = HttpStatus.BAD_REQUEST;
            code = ((BusinessException) error).getCode();
        } else if (error instanceof TechnicalException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            code = ((TechnicalException) error).getCode();
        } else if (error instanceof ValidationException) {
            status = HttpStatus.BAD_REQUEST;
            code = ((ValidationException) error).getCode();
            data = error.getMessage();
        }else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ApiResponse<Object> errorResponse = ApiResponse.builder()
                .code(code)
                .data(data)
                .build();

        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }
}