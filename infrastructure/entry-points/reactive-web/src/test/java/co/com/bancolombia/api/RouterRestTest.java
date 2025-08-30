package co.com.bancolombia.api;

import co.com.bancolombia.api.exceptions.GlobalExceptionHandler;
import co.com.bancolombia.api.request.UserRequest;
import co.com.bancolombia.api.response.ApiResponse;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.user.User;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, GlobalExceptionHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SaveUserUseCase saveUserUseCase;

    @MockitoBean
    private FilterUserByIdentificationUseCase filterUserByIdentificationUseCase;


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
                .build();
    }

    private User buildDomainUser() {
        return User.builder()
                .idUser(1)
                .names("Juan")
                .lastNames("Ortiz")
                .dateBirth(LocalDate.of(1995, 5, 20))
                .identityDocument("12345678")
                .address("Calle 123")
                .phone("3001234567")
                .email("juan@test.com")
                .baseSalary(BigDecimal.valueOf(5000000))
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



}
