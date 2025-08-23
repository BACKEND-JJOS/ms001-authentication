package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @InjectMocks
    private UserReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    private UserReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private final UserEntity userEntity = UserEntity.builder()
            .idUser(1)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@test.com")
            .identityDocument("123456789")
            .phone("555-1234")
            .idRol(2)
            .baseSalary(1000.0)
            .build();

    private final User user = new User(
            1,
            "John",
            "Doe",
            "john.doe@test.com",
            "123456789",
            "555-1234",
            2,
            1000.0
    );

    @Test
    void mustSaveValue() {
        when(repository.save(userEntity)).thenReturn(Mono.just(userEntity));
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        Mono<User> result = repositoryAdapter.save(user);

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }
}
