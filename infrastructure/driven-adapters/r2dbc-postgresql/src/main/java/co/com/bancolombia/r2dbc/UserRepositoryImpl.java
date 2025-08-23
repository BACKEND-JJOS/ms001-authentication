package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private UserReactiveRepositoryAdapter dao;

    @Override
    public Mono<User> save(User user) {
        return dao.save(user);
    }
}
