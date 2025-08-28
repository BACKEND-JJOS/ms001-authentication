package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class UserReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<User,UserEntity,Integer,UserReactiveRepository>
        implements UserRepository{


    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    public Mono<User> save(User user) {
        log.debug("MESSAGE_ADAPTER_LOG_TRACE: INIT save user email={}", user.getEmail());
        return super.save(user)
                .doOnSuccess(saved -> log.info("MESSAGE_ADAPTER_LOG_TRACE : User saved email={}", saved.getEmail()))
                .doOnError(err -> log.error("MESSAGE_ADAPTER_LOG_TRACE : DB error while saving user email={} - {}", user.getEmail(), err.getMessage()));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .doOnSubscribe(sub -> log.debug("MESSAGE_ADAPTER_LOG_TRACE : INIT findByEmail - email={}", email))
                .doOnSuccess(user -> {
                    if (user != null) {
                        log.info("MESSAGE_ADAPTER_LOG_TRACE : User found email={}", email);
                    } else {
                        log.info("MESSAGE_ADAPTER_LOG_TRACE : No user found email={}", email);
                    }
                })
                .doOnError(err -> log.error("MESSAGE_ADAPTER_LOG_TRACE : DB error while searching user email={} - {}", email, err.getMessage()))
                .map(this::toEntity);
    }

}
