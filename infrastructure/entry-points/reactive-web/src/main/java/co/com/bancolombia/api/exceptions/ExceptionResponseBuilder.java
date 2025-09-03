package co.com.bancolombia.api.exceptions;

import co.com.bancolombia.api.response.ApiResponse;

public class ExceptionResponseBuilder {
    public static ApiResponse<Object> buildResponse( String code, Object data) {
        return ApiResponse.builder()
                .code(code)
                .data(data)
                .build();
    }
}
