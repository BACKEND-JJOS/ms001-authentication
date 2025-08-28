
package co.com.bancolombia.api.validator;


import co.com.bancolombia.api.exceptions.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class GenericValidator {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> Mono<T> validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        return violations.isEmpty()
                ? Mono.just(object)
                : Mono.error(new ValidationException("Validation errors: " + formatErrors(violations)));
    }

    private static <T> String formatErrors(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
    }
}
