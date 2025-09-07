package co.com.bancolombia.exceptions;

import lombok.Getter;

@Getter
public class BusinessUnAuthorizedException  extends RuntimeException{

    private final String code;

    public BusinessUnAuthorizedException(String code) {
        this.code = code;
    }
}
