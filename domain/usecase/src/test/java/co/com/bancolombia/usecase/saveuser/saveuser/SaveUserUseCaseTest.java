package co.com.bancolombia.usecase.saveuser.saveuser;

import co.com.bancolombia.exceptions.BusinessException;
import co.com.bancolombia.responsecode.ResponseCode;
import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.saveuser.SaveUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private SaveUserUseCase saveUserUseCase;

    private User newUser;
    private User existingByEmail;
    private User existingByIdentification;
    private Rol adminRol;

    @BeforeEach
    void setUp() {
        adminRol = new Rol(1L, "ADMIN", "Administrador del sistema");

        newUser = User.builder()
                .idUser(null)
                .names("Jane")
                .lastNames("Smith")
                .email("jane.smith@example.com")
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("8000.0"))
                .rol(adminRol)
                .build();

        existingByEmail = User.builder()
                .idUser(1L)
                .names("Jane")
                .lastNames("Smith")
                .email("jane.smith@example.com")
                .identityDocument("87654321")
                .baseSalary(new BigDecimal("8000.0"))
                .rol(adminRol)
                .build();

        existingByIdentification = User.builder()
                .idUser(2L)
                .names("John")
                .lastNames("Doe")
                .email("john.doe@example.com")
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("8000.0"))
                .rol(adminRol)
                .build();
    }

    @Test
    void shouldSaveNewUserWhenNotExists() {
        when(rolRepository.findById(newUser.getRol().getIdRol())).thenReturn(Mono.just(adminRol));
        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Mono.empty());
        when(userRepository.findByIdentification(newUser.getIdentityDocument())).thenReturn(Mono.empty());
        when(userRepository.save(newUser)).thenReturn(Mono.just(newUser));

        Mono<User> result = saveUserUseCase.execute(newUser);

        StepVerifier.create(result)
                .expectNext(newUser)
                .verifyComplete();
    }

    @Test
    void shouldThrowBusinessExceptionWhenRoleNotExists() {
        when(rolRepository.findById(newUser.getRol().getIdRol())).thenReturn(Mono.empty());

        Mono<User> result = saveUserUseCase.execute(newUser);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getCode().equals(ResponseCode.ROLE_NOT_EXISTS))
                .verify();
    }

    @Test
    void shouldThrowBusinessExceptionWhenEmailAlreadyExists() {
        when(rolRepository.findById(newUser.getRol().getIdRol())).thenReturn(Mono.just(adminRol));
        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Mono.just(existingByEmail));

        Mono<User> result = saveUserUseCase.execute(newUser);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getCode().equals(ResponseCode.DUPLICATE_EMAIL))
                .verify();
    }

    @Test
    void shouldThrowBusinessExceptionWhenIdentificationAlreadyExists() {
        when(rolRepository.findById(newUser.getRol().getIdRol())).thenReturn(Mono.just(adminRol));
        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Mono.empty());
        when(userRepository.findByIdentification(newUser.getIdentityDocument())).thenReturn(Mono.just(existingByIdentification));

        Mono<User> result = saveUserUseCase.execute(newUser);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getCode().equals(ResponseCode.DUPLICATE_IDENTIFICATION))
                .verify();
    }
}
