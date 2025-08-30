package co.com.bancolombia.usecase.filteruserbyidentification;

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
class FilterUserByIdentificationUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FilterUserByIdentificationUseCase filterUserByIdentificationUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .idUser(1)
                .names("Jane")
                .lastNames("Smith")
                .email("jane.smith@example.com")
                .identityDocument("12345678")
                .dateBirth(LocalDate.of(1995, 5, 20))
                .baseSalary(new BigDecimal("8000.0"))
                .build();
    }

    @Test
    void shouldReturnUserWhenExists() {
        when(userRepository.findByIdentification("12345678")).thenReturn(Mono.just(user));

        Mono<User> result = filterUserByIdentificationUseCase.execute("12345678");

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenUserNotExists() {
        when(userRepository.findByIdentification("99999999")).thenReturn(Mono.empty());

        Mono<User> result = filterUserByIdentificationUseCase.execute("99999999");

        StepVerifier.create(result)
                .verifyComplete();
    }
}
