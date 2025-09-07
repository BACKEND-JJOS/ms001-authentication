package co.com.bancolombia.usecase.authuser;

import co.com.bancolombia.exceptions.BusinessUnAuthorizedException;
import co.com.bancolombia.responsecode.ResponseCode;
import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private AuthUserUseCase authUserUseCase;

    private User user;
    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol(1L, "ADMIN", "Administrador");

        user = User.builder()
                .idUser(1L)
                .names("Juan")
                .lastNames("Ortiz")
                .email("juan@test.com")
                .identityDocument("12345678")
                .dateBirth(LocalDate.of(1990, 1, 1))
                .baseSalary(BigDecimal.valueOf(5000))
                .rol(rol)
                .password("hashedPassword")
                .build();
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(user));
        when(rolRepository.findById(rol.getIdRol())).thenReturn(Mono.just(rol));

        Mono<User> result = authUserUseCase.authenticate(user.getEmail());

        StepVerifier.create(result)
                .expectNextMatches(u ->
                        u.getEmail().equals(user.getEmail()) &&
                                u.getRol().equals(rol))
                .verifyComplete();
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.empty());

        Mono<User> result = authUserUseCase.authenticate(user.getEmail());

        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof BusinessUnAuthorizedException &&
                                ((BusinessUnAuthorizedException) ex).getCode().equals(ResponseCode.INVALID_CREDENTIALS))
                .verify();
    }

    @Test
    void shouldReturnEmptyWhenRoleNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(user));
        when(rolRepository.findById(rol.getIdRol())).thenReturn(Mono.empty());

        Mono<User> result = authUserUseCase.authenticate(user.getEmail());

        StepVerifier.create(result)
                .verifyComplete(); // se completa vacío porque no hay rol
    }
}
