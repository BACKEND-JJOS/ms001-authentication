package co.com.bancolombia.api.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserResponse {
    private Long idUser;
    private String names;
    private String lastNames;
    private String identityDocument;
    private String email;
    private BigDecimal baseSalary;
}
