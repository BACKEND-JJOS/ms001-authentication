package co.com.bancolombia.usecase.saveuser.saveuser;

import co.com.bancolombia.model.exceptions.BusinessException;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SaveUserUseCase saveUserUseCase;

    private User newUser;
    private User existingUser;

    @BeforeEach
    void setUp() {
        newUser = User.builder()
                .idUser(null)
                .names("Jane")
                .lastNames("Smith")
                .email("jane.smith@example.com")
                .baseSalary(8000.0)
                .build();

        existingUser = User.builder()
                .idUser(1)
                .names("Jane")
                .lastNames("Smith")
                .email("jane.smith@example.com")
                .baseSalary(8000.0)
                .build();
    }


    @Test
    void shouldSaveNewUserWhenNotExists() {
        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Mono.empty());
        when(userRepository.save(newUser)).thenReturn(Mono.just(newUser));

        Mono<User> result = saveUserUseCase.execute(newUser);

        StepVerifier.create(result)
                .expectNext(newUser)
                .verifyComplete();
    }

    @Test
    void shouldThrowBusinessExceptionWhenUserAlreadyExists() {


        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Mono.just(existingUser));

        Mono<User> result = saveUserUseCase.execute(existingUser);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                throwable.getMessage().equals("User already exists"))
                .verify();
    }
}
