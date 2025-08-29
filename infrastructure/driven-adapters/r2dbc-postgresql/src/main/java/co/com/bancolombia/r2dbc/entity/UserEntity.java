package co.com.bancolombia.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("usuario")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column("id_usuario")
    private Integer idUser;

    @Column("nombres")
    private String names;

    @Column("apellidos")
    private String lastNames;

    @Column("fecha_nacimiento")
    private LocalDate dateBirth;

    @Column("documento_identidad")
    private String identityDocument;

    @Column("direccion")
    private String address;

    @Column("telefono")
    private String phone;

    @Column("correo_electronico")
    private String email;

    @Column("id_rol")
    private Integer idRol;

    @Column("salario_base")
    private BigDecimal baseSalary;
}
