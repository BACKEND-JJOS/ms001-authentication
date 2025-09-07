package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.response.UserResponse;
import co.com.bancolombia.model.user.User;

public class UserResponseMapper {
    private UserResponseMapper () {}

    public static UserResponse toResponse(User userDomain){
        return UserResponse.builder()
                .idUser(userDomain.getIdUser())
                .names(userDomain.getNames())
                .lastNames(userDomain.getLastNames())
                .identityDocument(userDomain.getIdentityDocument())
                .email(userDomain.getEmail())
                .baseSalary(userDomain.getBaseSalary())
                .build();
    }
}
