package co.com.bancolombia.api.exceptions;

import lombok.Getter;

@Getter
public class ForbiddenUnAuthorizedException  extends RuntimeException{

    private final String code;

    public ForbiddenUnAuthorizedException(String code) {
        this.code = code;
    }
}

