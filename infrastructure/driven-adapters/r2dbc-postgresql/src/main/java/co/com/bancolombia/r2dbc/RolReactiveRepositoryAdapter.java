package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.exceptions.TechnicalException;
import co.com.bancolombia.model.responsecode.ResponseCode;
import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.entity.RolEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class RolReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<Rol, RolEntity,Long,RolReactiveRepository>
        implements RolRepository {


    public RolReactiveRepositoryAdapter(RolReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Rol.class));
    }

    @Override
    public Mono<Rol> findById(Long idRol) {
        return repository.findById(idRol)
                .doOnSubscribe(sub -> log.debug("MESSAGE_ADAPTER_LOG_TRACE : INIT findById - id={}", idRol))
                .doOnSuccess(rol -> {
                    if (rol != null) {
                        log.info("MESSAGE_ADAPTER_LOG_TRACE : Rol found id={}", rol);
                    } else {
                        log.info("MESSAGE_ADAPTER_LOG_TRACE : No rol found email={}", rol);
                    }
                })
                .map(this::toEntity)
                .onErrorMap(throwable -> {
                    log.error("MESSAGE_ADAPTER_LOG_TRACE : DB error while searching user email={} - {}", idRol, throwable.getMessage());
                    return new TechnicalException(ResponseCode.DATA_BASE_FAILED);
                });
    }


}
