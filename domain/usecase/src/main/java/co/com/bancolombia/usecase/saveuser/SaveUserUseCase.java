package co.com.bancolombia.usecase.saveuser;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SaveUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> execute(User user){
        return userRepository.save(user);
    }
}
