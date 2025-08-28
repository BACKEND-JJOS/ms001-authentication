package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserReactiveRepositoryAdapter dao;

    @Override
    public Mono<User> save(User user) {
        return dao.save(user);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return dao.findByEmail(email);
    }
}
