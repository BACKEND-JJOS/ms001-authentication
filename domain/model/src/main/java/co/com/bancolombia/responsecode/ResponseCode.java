package co.com.bancolombia.responsecode;

public class ResponseCode {
    private ResponseCode() {
        throw new IllegalStateException("Utility class");
    }
    public static final String DUPLICATE_EMAIL = "MS001-AUTH-ERROR001";
    public static final String DUPLICATE_IDENTIFICATION = "MS001-AUTH-ERROR002";
    public static final String USER_NOT_EXISTS = "MS001-AUTH-ERROR003";
    public static final String TECHNICAL_ERROR = "MS001-AUTH-ERROR004";
    public static final String DATA_CORRUPTED = "MS001-AUTH-ERROR005";
    public static final String DATA_BASE_FAILED = "MS001-AUTH-ERROR006";
    public static final String ROLE_NOT_EXISTS = "MS001-AUTH-ERROR007";
    public static final String INVALID_CREDENTIALS = "MS001-AUTH-ERROR008";
    public static final String UNAUTHORIZED = "MS001-AUTH-ERROR009";
    public static final String FORBIDDEN = "MS001-AUTH-ERROR010";

    public static final String USER_CREATED_SUCCESSFULLY = "MS001-AUTH-SUCCESS001";
    public static final String USER_FILTERED_SUCCESSFULLY = "MS001-AUTH-SUCCESS002";
    public static final String USER_LOGIN_SUCCESSFULLY = "MS001-AUTH-SUCCESS003";
}
