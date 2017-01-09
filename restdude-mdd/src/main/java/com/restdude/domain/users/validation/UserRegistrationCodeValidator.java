/**
 * Restdude
 * -------------------------------------------------------------------
 * Module restdude-mdd, https://manosbatsis.github.io/restdude/restdude-mdd
 * <p>
 * Full stack, high level framework for horizontal, model-driven application hackers.
 * <p>
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.restdude.domain.users.validation;


import com.restdude.domain.users.model.UserRegistrationCode;
import com.restdude.domain.users.repository.UserRegistrationCodeRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validate the registration code, if any, is available
 */
@Component
public class UserRegistrationCodeValidator implements ConstraintValidator<UserRegistrationCodeConstraint, UserRegistrationCode> {

    private UserRegistrationCodeRepository userRegistrationCodeRepository;

    public void setUserRegistrationCodeRepository(UserRegistrationCodeRepository userRegistrationCodeRepository) {
        this.userRegistrationCodeRepository = userRegistrationCodeRepository;
    }

    @Override
    public void initialize(UserRegistrationCodeConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(UserRegistrationCode value, ConstraintValidatorContext context) {
        boolean valid = true;
        if (value != null) {
            valid = false;
            if (StringUtils.isNotBlank(value.getId())) {
                UserRegistrationCode code = this.userRegistrationCodeRepository.findOne(value.getId());
                valid = code != null && code.getAvailable();
            }
        }
        return valid;
    }
}