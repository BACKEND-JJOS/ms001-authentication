package co.com.bancolombia.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

public class UserRequest {
    @NotBlank(message = "names is required")
    private String names;

    @NotBlank(message = "last names is required")
    private String lastNames;

    @NotBlank(message = "date of birth is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "date of birth must follow the format yyyy-MM-dd"
    )
    private String dateBirth;

    @NotBlank(message = "idenity document is required")
    private String identityDocument;

    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "phone is required")
    private String phone;

    @NotBlank(message = "email is required")
    @Email(message = "email format is invalid")
    private String email;

    @NotNull(message = "base salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "base salary must be >= 0")
    @DecimalMax(value = "15000000.0", inclusive = true, message = "base salary must be <= 15000000")
    private BigDecimal baseSalary;

    @NotBlank(message = "password is required")
    private String password;

    @NotNull(message = "id rol is required")
    private Long idRol;
}
