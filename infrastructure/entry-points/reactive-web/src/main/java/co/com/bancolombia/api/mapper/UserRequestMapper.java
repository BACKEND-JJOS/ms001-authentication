package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.request.UserRequest;
import co.com.bancolombia.model.user.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserRequestMapper {

    private UserRequestMapper() {}

    public static User toDomain(UserRequest request) {
        LocalDate birthDate = LocalDate.parse(request.getDateBirth(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return User.builder()
                .idUser(null)
                .names(request.getNames())
                .lastNames(request.getLastNames())
                .dateBirth(birthDate)
                .identityDocument(request.getIdentityDocument())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .idRole(null)
                .baseSalary(request.getBaseSalary())
                .build();
    }
}
