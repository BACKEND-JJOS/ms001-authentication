package co.com.bancolombia.usecase.saveuser;

import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class SaveUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> execute(User user) {
        return userRepository.findByEmail(user.getEmail())
                .hasElement()
                .flatMap(emailExists -> emailExists
                        ? Mono.error(new BusinessException("User email already exists"))
                        : Mono.just(user))
                .flatMap(userValidateWithoutEmail -> userRepository.findByIdentification(userValidateWithoutEmail.getIdentityDocument())
                        .hasElement()
                        .flatMap(idExists -> idExists
                                ? Mono.error(new BusinessException("User identification already exists"))
                                : userRepository.save(userValidateWithoutEmail)
                        )
                );
    }





}
