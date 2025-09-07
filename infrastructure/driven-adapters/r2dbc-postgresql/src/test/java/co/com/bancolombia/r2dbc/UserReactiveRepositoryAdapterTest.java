package co.com.bancolombia.r2dbc;

import co.com.bancolombia.exceptions.TechnicalException;
import co.com.bancolombia.responsecode.ResponseCode;
import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @Mock
    private UserReactiveRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserReactiveRepositoryAdapter adapter;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {

        Rol rol = new Rol(1L, "ADMIN", "Administrador del sistema");

        user = User.builder()
                .idUser(1L)
                .names("Jane")
                .lastNames("Doe")
                .email("jane@example.com")
                .identityDocument("12345678")
                .dateBirth(LocalDate.of(1990, 1, 1))
                .baseSalary(BigDecimal.valueOf(8000))
                .rol(rol)
                .password("hashedPassword")
                .build();

        userEntity = UserEntity.builder()
                .idUser(1L)
                .names("Jane")
                .lastNames("Doe")
                .email("jane@example.com")
                .identityDocument("12345678")
                .dateBirth(LocalDate.of(1990, 1, 1))
                .baseSalary(BigDecimal.valueOf(8000))
                .password("hashedPassword")
                .build();
    }

    @Test
    void shouldSaveUserSuccessfully() {
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(userEntity));

        StepVerifier.create(adapter.save(user))
                .expectNextMatches(saved -> saved.getEmail().equals(user.getEmail()))
                .verifyComplete();
    }

    @Test
    void shouldThrowTechnicalExceptionWhenSaveFails() {
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.error(new RuntimeException("DB down")));

        StepVerifier.create(adapter.save(user))
                .expectErrorMatches(ex ->
                        ex instanceof TechnicalException &&
                                ((TechnicalException) ex).getCode().equals(ResponseCode.DATA_BASE_FAILED))
                .verify();
    }

    @Test
    void shouldFindUserByEmailSuccessfully() {
        when(repository.findByEmail(user.getEmail())).thenReturn(Mono.just(userEntity));

        StepVerifier.create(adapter.findByEmail(user.getEmail()))
                .expectNextMatches(found -> found.getEmail().equals(user.getEmail()))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenUserByEmailNotFound() {
        when(repository.findByEmail(user.getEmail())).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByEmail(user.getEmail()))
                .verifyComplete();
    }

    @Test
    void shouldThrowTechnicalExceptionWhenFindByEmailFails() {
        when(repository.findByEmail(user.getEmail())).thenReturn(Mono.error(new RuntimeException("Query error")));

        StepVerifier.create(adapter.findByEmail(user.getEmail()))
                .expectErrorMatches(ex ->
                        ex instanceof TechnicalException &&
                                ((TechnicalException) ex).getCode().equals(ResponseCode.DATA_BASE_FAILED))
                .verify();
    }

    @Test
    void shouldFindUserByIdentificationSuccessfully() {
        when(repository.findByIdentityDocument(user.getIdentityDocument())).thenReturn(Mono.just(userEntity));

        StepVerifier.create(adapter.findByIdentification(user.getIdentityDocument()))
                .expectNextMatches(found -> found.getIdentityDocument().equals(user.getIdentityDocument()))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenUserByIdentificationNotFound() {
        when(repository.findByIdentityDocument(user.getIdentityDocument())).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByIdentification(user.getIdentityDocument()))
                .verifyComplete();
    }

    @Test
    void shouldThrowTechnicalExceptionWhenFindByIdentificationFails() {
        when(repository.findByIdentityDocument(user.getIdentityDocument()))
                .thenReturn(Mono.error(new RuntimeException("Query error")));

        StepVerifier.create(adapter.findByIdentification(user.getIdentityDocument()))
                .expectErrorMatches(ex ->
                        ex instanceof TechnicalException &&
                                ((TechnicalException) ex).getCode().equals(ResponseCode.DATA_BASE_FAILED))
                .verify();
    }
}
