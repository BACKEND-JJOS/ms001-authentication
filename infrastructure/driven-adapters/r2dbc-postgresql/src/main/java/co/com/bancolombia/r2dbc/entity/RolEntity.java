package co.com.bancolombia.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("rol")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RolEntity {

    @Id
    @Column("id_rol")
    private Long idRol;

    @Column("nombre")
    private String name;

    @Column("descripcion")
    private String description;
}
