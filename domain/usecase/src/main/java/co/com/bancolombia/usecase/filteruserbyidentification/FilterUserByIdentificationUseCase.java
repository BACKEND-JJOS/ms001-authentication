package co.com.bancolombia.usecase.filteruserbyidentification;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FilterUserByIdentificationUseCase {

    private final UserRepository userRepository;

    public Mono<User> execute(String identification){
        return userRepository.findByIdentification(identification);
    }
}
