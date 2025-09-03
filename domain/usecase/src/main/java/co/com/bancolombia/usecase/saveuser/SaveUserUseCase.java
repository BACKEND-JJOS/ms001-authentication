package co.com.bancolombia.usecase.saveuser;

import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.responsecode.ResponseCode;
import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class SaveUserUseCase {

    private final UserRepository userRepository;

    private final RolRepository rolRepository;

    public Mono<User> execute(User user) {
        return  rolRepository.findById(user.getRol().getIdRol())
                .switchIfEmpty(Mono.error(new BusinessException(
                        ResponseCode.ROLE_NOT_EXISTS
                )))
                .map(rol -> User.builder()
                        .rol(rol)
                        .build()
                ).flatMap(userWithRol -> userRepository.findByEmail(user.getEmail())
                        .hasElement()
                        .flatMap(emailExists -> Boolean.TRUE.equals(emailExists)
                                ? Mono.error(new BusinessException(ResponseCode.DUPLICATE_EMAIL))
                                : Mono.just(user))
                        .flatMap(userValidateWithoutEmail -> userRepository.findByIdentification(userValidateWithoutEmail.getIdentityDocument())
                                .hasElement()
                                .flatMap(idExists -> Boolean.TRUE.equals(idExists)
                                        ? Mono.error(new BusinessException(ResponseCode.DUPLICATE_IDENTIFICATION))
                                        : userRepository.save(userValidateWithoutEmail)
                                )
                        )
                );
    }
}
