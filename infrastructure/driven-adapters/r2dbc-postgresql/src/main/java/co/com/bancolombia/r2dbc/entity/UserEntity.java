package co.com.bancolombia.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Table("usuario")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column("id_usuario")
    private Integer idUser;

    @Column("nombre")
    private String firstName;

    @Column("apellido")
    private String lastName;

    @Column("email")
    private String email;

    @Column("documento_identidad")
    private String identityDocument;

    @Column("telefono")
    private String phone;

    @Column("id_rol")
    private Integer idRol;

    @Column("salario_base")
    private Double baseSalary;
}
