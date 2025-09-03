package co.com.bancolombia.model.rol;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Rol {
    private Long idRol;
    private String name;
    private String description;
}