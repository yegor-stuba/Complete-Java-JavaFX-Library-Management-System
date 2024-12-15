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

        if (book.getAvailableCopies() < 0) {
            errors.rejectValue("availableCopies", "field.invalid", "Available copies must be non-negative");
        }

        if (book.getIsbn() != null && !book.getIsbn().matches("^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$")) {
            errors.rejectValue("isbn", "field.invalid", "Invalid ISBN format : must be 10 digits");
        }

        // Validate borrowing rules
        if (book.getBorrower() != null) {
            // Check if user already has a book borrowed
            if (book.getBorrower().getActiveBorrowCount() >= 1) {
                errors.rejectValue("borrower", "field.invalid", "User can only borrow one book at a time");
            }
        }

        // Validate available copies
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            errors.rejectValue("availableCopies", "field.invalid", "Available copies cannot exceed total copies");
        }
    }
}
