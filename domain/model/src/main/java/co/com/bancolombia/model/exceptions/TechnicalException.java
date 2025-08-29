package co.com.bancolombia.model.exceptions;

import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {

    private final String code;

    public TechnicalException(String code) {
       this.code = code;
    }
}
