package co.com.bancolombia.api.exceptions;

import co.com.bancolombia.exceptions.BusinessException;
import co.com.bancolombia.exceptions.BusinessUnAuthorizedException;
import co.com.bancolombia.exceptions.TechnicalException;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class ExceptionHttpStatusMapper {

    private static final Map<Class<? extends Throwable>, HttpStatus> STATUS_MAP = new HashMap<>();

    static {
        STATUS_MAP.put(BusinessException.class, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(ValidationException.class, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(BusinessUnAuthorizedException.class, HttpStatus.UNAUTHORIZED);
        STATUS_MAP.put(ForbiddenUnAuthorizedException.class, HttpStatus.FORBIDDEN);
        STATUS_MAP.put(TechnicalException.class, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static HttpStatus resolveHttpStatus(Throwable error) {
        return STATUS_MAP.getOrDefault(error.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
