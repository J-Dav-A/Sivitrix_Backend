package co.edu.uniquindio.sivitrix.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * SWR-35 / SWR-32: valida el formato del NIT (digitos y, si se incluye,
 * el digito de verificacion segun el algoritmo de la DIAN).
 * Formato aceptado: "900123456" o "900123456-7".
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NitValidator.class)
@Documented
public @interface ValidNit {

    String message() default "el NIT no tiene un formato valido (numero o numero-digitoVerificacion)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
