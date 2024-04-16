package com.dominest.dominestbackend.global.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    /**
     * @param value 01[016789]-xxx(x)-xxxx 형식이어야 한다.
     *              첫번째 자리는 01x의 3자리 숫자, 두번째 자리는 3/4자리 숫자, 세번째 자리는 4자리 숫자여야 한다.
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.matches("(01[016789])-(\\d{3,4})-(\\d{4})");
    }
}
