package com.studyshare.server.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "books")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "isbn", unique = true, nullable = false)
    private String isbn;

    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;

    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private User borrower;


    @PrePersist
    @PreUpdate
    private void validateCopies() {
        if (totalCopies == null) {
            totalCopies = availableCopies;
        }
        if (availableCopies < 0 || availableCopies > totalCopies) {
            throw new IllegalStateException("Invalid copy count");
        }
    }
}