package co.com.bancolombia.usecase.authuser;

import co.com.bancolombia.exceptions.BusinessUnAuthorizedException;
import co.com.bancolombia.responsecode.ResponseCode;
import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class AuthUserUseCase {

    private final UserRepository userRepository;

    private final RolRepository rolRepository;

    public Mono<User> authenticate(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new BusinessUnAuthorizedException(ResponseCode.INVALID_CREDENTIALS)))
                .flatMap(user ->
                        rolRepository.findById(user.getRol().getIdRol())
                                .map(rol -> {
                                    user.setRol(rol);
                                    return user;
                                })
                );
    }

}
