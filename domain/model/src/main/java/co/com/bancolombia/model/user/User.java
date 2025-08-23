package co.com.bancolombia.model.user;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class User {

    private Integer idUser;
    private String firstName;
    private String lastName;
    private String email;
    private String identityDocument;
    private String phone;
    private Integer idRole;
    private Double baseSalary;
}
