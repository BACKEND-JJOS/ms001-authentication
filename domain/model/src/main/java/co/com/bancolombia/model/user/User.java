package co.com.bancolombia.model.user;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class User {

    private Integer idUser;
    private String names;
    private String lastNames;
    private LocalDate dateBirth;
    private String identityDocument;
    private String address;
    private String phone;
    private String email;
    private Integer idRole;
    private BigDecimal baseSalary;
}
