package co.com.bancolombia.usecase.getuserbyid;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetUserByIdUseCase {

    private final UserRepository userRepository;

    public Mono<User> execute(Long id){
        return userRepository.findById(id);
    }
}
