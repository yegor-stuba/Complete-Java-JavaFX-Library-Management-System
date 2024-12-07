package com.studyshare.server.validation;

import com.studyshare.common.dto.UserDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return UserDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO user = (UserDTO) target;

        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.rejectValue("email", "invalid.email", "Invalid email format");
        }

        if (user.getUsername().length() < 3) {
            errors.rejectValue("username", "invalid.username", "Username must be at least 3 characters");
        }

        validatePassword(user.getPassword(), errors);
    }

    private void validatePassword(String password, Errors errors) {
        if (password == null || password.length() < 8) {
            errors.rejectValue("password", "invalid.password.length", "Password must be at least 8 characters");
            return;
        }

        if (!password.matches(".*[A-Z].*")) {
            errors.rejectValue("password", "invalid.password.uppercase", "Password must contain uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            errors.rejectValue("password", "invalid.password.lowercase", "Password must contain lowercase letter");
        }

        if (!password.matches(".*\\d.*")) {
            errors.rejectValue("password", "invalid.password.digit", "Password must contain a digit");
        }

        if (!password.matches(".*[!@#$%^&*()].*")) {
            errors.rejectValue("password", "invalid.password.special", "Password must contain special character");
        }
    }
}
