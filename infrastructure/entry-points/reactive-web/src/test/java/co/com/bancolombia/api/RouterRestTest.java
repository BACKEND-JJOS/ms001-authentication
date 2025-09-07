package co.com.bancolombia.api;

import co.com.bancolombia.api.auth.JwtUtils;
import co.com.bancolombia.api.auth.PasswordUtils;
import co.com.bancolombia.api.exceptions.GlobalExceptionHandler;
import co.com.bancolombia.api.request.UserLoginRequest;
import co.com.bancolombia.api.request.UserRequest;
import co.com.bancolombia.api.response.ApiResponse;
import co.com.bancolombia.exceptions.BusinessException;
import co.com.bancolombia.exceptions.BusinessUnAuthorizedException;
import co.com.bancolombia.responsecode.ResponseCode;
import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.authuser.AuthUserUseCase;
import co.com.bancolombia.usecase.filteruserbyidentification.FilterUserByIdentificationUseCase;
import co.com.bancolombia.usecase.saveuser.SaveUserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;


@ContextConfiguration(classes = {
        RouterRest.class,
        Handler.class,
        GlobalExceptionHandler.class,
        TestSecurityConfig.class,
        JwtUtils.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SaveUserUseCase saveUserUseCase;

    @MockitoBean
    private FilterUserByIdentificationUseCase filterUserByIdentificationUseCase;

    @MockitoBean
    private AuthUserUseCase authUserUseCase;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private PasswordUtils passwordUtils;


    private UserRequest buildValidUserRequest() {
        return UserRequest.builder()
                .names("Juan")
                .lastNames("Ortiz")
                .dateBirth("1995-05-20")
                .identityDocument("12345678")
                .address("Calle 123")
                .phone("3001234567")
                .email("juan@test.com")
                .baseSalary(BigDecimal.valueOf(5000000))
                .idRol(1L)
                .password("12345")
                .build();
    }

    private User buildDomainUser() {
        return User.builder()
                .idUser(1L)
                .names("Juan")
                .lastNames("Ortiz")
                .dateBirth(LocalDate.of(1995, 5, 20))
                .identityDocument("12345678")
                .address("Calle 123")
                .phone("3001234567")
                .email("juan@test.com")
                .baseSalary(BigDecimal.valueOf(5000000))
                .password("encodedPass")
                .rol(Rol.builder().idRol(1L).name("ROL_ADMIN").build())
                .build();
    }

    @Test
    void testPOSTCreateUser_CREATED() {
        given(saveUserUseCase.execute(any()))
                .willReturn(Mono.just(buildDomainUser()));

        webTestClient.post()
                .uri("/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildValidUserRequest())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ApiResponse.class)
                .value(apiResponse -> {
                    Assertions.assertThat(apiResponse.getData()).isNotNull();
                });
    }


    @Test
    void testGETUserByIdentification_OK() {
        given(filterUserByIdentificationUseCase.execute("12345678"))
                .willReturn(Mono.just(buildDomainUser()));

        webTestClient.get()
                .uri("/v1/user/{identification}", "12345678")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiResponse<User>>() {})
                .value(apiResponse -> {
                    Assertions.assertThat(apiResponse.getData()).isNotNull();
                    Assertions.assertThat(apiResponse.getData().getIdentityDocument()).isEqualTo("12345678");
                    Assertions.assertThat(apiResponse.getData().getNames()).isEqualTo("Juan");
                });
    }

    @Test
    void testPOSTCreateUser_ValidationError() {
        UserRequest invalidRequest = UserRequest.builder()
                .lastNames("Ortiz")
                .dateBirth("1995-05-20")
                .identityDocument("12345678")
                .address("Calle 123")
                .phone("3001234567")
                .email("juan@test.com")
                .baseSalary(BigDecimal.valueOf(5000000))
                .build();

        webTestClient.post()
                .uri("/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPOSTCreateUser_ErrorInUseCase() {
        given(saveUserUseCase.execute(any()))
                .willReturn(Mono.error(new BusinessException("Error mock")));

        webTestClient.post()
                .uri("/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildValidUserRequest())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApiResponse.class)
                .value(apiResponse -> {
                    Assertions.assertThat(apiResponse.getCode()).isEqualTo("Error mock");
                });
    }

    @Test
    void testPOSTLogin_Success() {
        UserLoginRequest request = new UserLoginRequest("juan@test.com", "1234");

        given(authUserUseCase.authenticate(anyString())).willReturn(Mono.just(buildDomainUser()));
        given(passwordUtils.matches("1234", "encodedPass")).willReturn(true);
        given(jwtUtils.createToken(anyString(), anyString(), anyString(), any())).willReturn("fakeToken");

        webTestClient.post()
                .uri("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class)
                .value(apiResponse -> {
                    Map<String, String> data = (Map<String, String>) apiResponse.getData();
                    Assertions.assertThat(data.get("token")).isEqualTo("fakeToken");
                    Assertions.assertThat(apiResponse.getCode()).isEqualTo(ResponseCode.USER_LOGIN_SUCCESSFULLY);
                });
    }

    @Test
    void testPOSTLogin_InvalidPassword() {
        UserLoginRequest request = UserLoginRequest.builder()
                .email("juan@test.com")
                .password("wrongPass")
                .build();

        given(authUserUseCase.authenticate(anyString())).willReturn(Mono.just(buildDomainUser()));
        given(passwordUtils.matches("wrongPass", "encodedPass")).willReturn(false);

        webTestClient.post()
                .uri("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ApiResponse.class)
                .value(apiResponse -> {
                    Assertions.assertThat(apiResponse.getCode()).isEqualTo(ResponseCode.INVALID_CREDENTIALS);
                });
    }

    @Test
    void testPOSTLogin_UserNotFound() {
        UserLoginRequest request = new UserLoginRequest("notfound@test.com", "1234");

        given(authUserUseCase.authenticate(anyString()))
                .willReturn(Mono.error(new BusinessUnAuthorizedException(ResponseCode.USER_NOT_EXISTS)));

        webTestClient.post()
                .uri("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ApiResponse.class)
                .value(apiResponse -> {
                    Assertions.assertThat(apiResponse.getCode()).isEqualTo(ResponseCode.USER_NOT_EXISTS);
                });
    }



}
