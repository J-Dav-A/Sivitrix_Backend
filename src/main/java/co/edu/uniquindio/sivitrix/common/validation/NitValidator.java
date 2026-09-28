package co.edu.uniquindio.sivitrix.common.validation;

import co.edu.uniquindio.sivitrix.common.util.NitUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NitValidator implements ConstraintValidator<ValidNit, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // dejar que @NotBlank se encargue de ese caso
        }
        return NitUtils.esValido(value);
    }
}
