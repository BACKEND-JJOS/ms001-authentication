package co.com.bancolombia.r2dbc;

import co.com.bancolombia.exceptions.TechnicalException;
import co.com.bancolombia.responsecode.ResponseCode;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public Mono<User> save(User user) {
        log.debug("MESSAGE_ADAPTER_LOG_TRACE: INIT save user email={}", user.getEmail());
        return Mono.just(user)
                .map(UserMapper::toEntity)
                .flatMap(repository::save)
                .map(entity -> UserMapper.toDomain(entity, user.getRol()))
                .doOnSuccess(saved -> log.info("MESSAGE_ADAPTER_LOG_TRACE : User saved email={}", saved.getEmail()))
                .doOnError(err -> log.error("MESSAGE_ADAPTER_LOG_TRACE : DB error while saving user email={} - {}", user.getEmail(), err.getMessage()))
                .onErrorMap(throwable -> new TechnicalException(ResponseCode.DATA_BASE_FAILED));
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
                .map(UserMapper::toDomain)
                .onErrorMap(throwable -> {
                    log.error("MESSAGE_ADAPTER_LOG_TRACE : DB error while searching user email={} - {}", email, throwable.getMessage());
                    return new TechnicalException(ResponseCode.DATA_BASE_FAILED);
                });
    }

    @Override
    public Mono<User> findByIdentification(String identification) {
        return repository.findByIdentityDocument(identification)
                .doOnSubscribe(sub -> log.debug("MESSAGE_ADAPTER_LOG_TRACE : INIT findByIdentityDocument - id={}", identification))
                .doOnSuccess(user -> {
                    if (user != null) {
                        log.info("MESSAGE_ADAPTER_LOG_TRACE : User found identification={}", identification);
                    } else {
                        log.info("MESSAGE_ADAPTER_LOG_TRACE : No user found identification={}", identification);
                    }
                })
                .map(UserMapper::toDomain)
                .onErrorMap(throwable -> {
                    log.error("MESSAGE_ADAPTER_R2DBC_LOG_TRACE : DB error while finding user by identification ={} - {}",identification, throwable.getMessage());
                    return new TechnicalException(ResponseCode.DATA_BASE_FAILED);
                });
    }

    @Override
    public Mono<User> findById(Long id) {
        return repository.findById(Math.toIntExact(id))
                .doOnSubscribe(sub -> log.debug("MESSAGE_ADAPTER_LOG_TRACE : INIT findById id={}", id))
                .doOnSuccess(user -> {
                    if (user != null) {
                        log.info("MESSAGE_ADAPTER_LOG_TRACE : User found id={}", id);
                    } else {
                        log.info("MESSAGE_ADAPTER_LOG_TRACE : No user found id={}", id);
                    }
                })
                .map(UserMapper::toDomain)
                .onErrorMap(throwable -> {
                    log.error("MESSAGE_ADAPTER_R2DBC_LOG_TRACE : DB error while finding user by id ={} - {}",id, throwable.getMessage());
                    return new TechnicalException(ResponseCode.DATA_BASE_FAILED);
                });
    }

}
