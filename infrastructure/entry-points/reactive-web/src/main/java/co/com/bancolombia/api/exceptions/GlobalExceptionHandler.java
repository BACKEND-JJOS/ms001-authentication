package co.com.bancolombia.api.exceptions;

import co.com.bancolombia.exceptions.BusinessException;
import co.com.bancolombia.exceptions.BusinessUnAuthorizedException;
import co.com.bancolombia.exceptions.TechnicalException;
import co.com.bancolombia.responsecode.ResponseCode;
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

        HttpStatus status = ExceptionHttpStatusMapper.resolveHttpStatus(error);

        String code = resolveErrorCode(error);
        Object data = (error instanceof ValidationException ve) ? ve.getMessage() : null;

        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ExceptionResponseBuilder.buildResponse(code, data));
    }
    private String resolveErrorCode(Throwable error) {
        if (error instanceof BusinessException be) return be.getCode();
        if (error instanceof TechnicalException te)return te.getCode();
        if (error instanceof ValidationException ve) return ve.getCode();
        if (error instanceof BusinessUnAuthorizedException ue) return ue.getCode();
        if (error instanceof ForbiddenUnAuthorizedException fe) return fe.getCode();
        return ResponseCode.TECHNICAL_ERROR;
    }
}