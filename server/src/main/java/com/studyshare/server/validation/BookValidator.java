package com.studyshare.server.validation;

import com.studyshare.common.dto.BookDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class BookValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return BookDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BookDTO book = (BookDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "field.required", "Title is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "author", "field.required", "Author is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "isbn", "field.required", "ISBN is required");

        if (book.getAvailableCopies() != null && book.getAvailableCopies() < 0) {
            errors.rejectValue("availableCopies", "field.invalid", "Available copies must be non-negative");
        }

        if (book.getIsbn() != null && !book.getIsbn().matches("^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$")) {
            errors.rejectValue("isbn", "field.invalid", "Invalid ISBN format");
        }
    }
}
