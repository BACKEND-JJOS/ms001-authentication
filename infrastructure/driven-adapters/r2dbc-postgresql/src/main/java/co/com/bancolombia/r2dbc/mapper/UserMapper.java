package co.com.bancolombia.r2dbc.mapper;

import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.entity.UserEntity;

public class UserMapper {

    public static UserEntity toEntity(User user){
        return UserEntity.builder()
                .idUser(user.getIdUser())
                .names(user.getNames())
                .lastNames(user.getLastNames())
                .dateBirth(user.getDateBirth())
                .identityDocument(user.getIdentityDocument())
                .address(user.getAddress())
                .phone(user.getPhone())
                .email(user.getEmail())
                .idRol(user.getRol() != null ? user.getRol().getIdRol() : null)
                .baseSalary(user.getBaseSalary())
                .password(user.getPassword())
                .build();
    }

    public static User toDomain(UserEntity entity, Rol rol) {
        return buildUser(entity, rol);
    }

    public static User toDomain(UserEntity entity) {
        return buildUser(entity, null);
    }

    private static User buildUser(UserEntity entity, Rol rol) {
        return User.builder()
                .idUser(entity.getIdUser())
                .names(entity.getNames())
                .lastNames(entity.getLastNames())
                .dateBirth(entity.getDateBirth())
                .identityDocument(entity.getIdentityDocument())
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .baseSalary(entity.getBaseSalary())
                .rol(rol == null ? Rol.builder().idRol(entity.getIdRol()).build() : rol)
                .password(entity.getPassword())
                .build();
    }

}
