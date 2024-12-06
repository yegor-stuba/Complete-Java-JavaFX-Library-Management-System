package com.studyshare.server.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "book_generator")
    @TableGenerator(name = "book_generator", table = "hibernate_sequences",
            pkColumnName = "sequence_name", valueColumnName = "next_val",
            pkColumnValue = "books", initialValue = 1, allocationSize = 1)
    private Long bookId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true)
    private String isbn;

    private Integer availableCopies;

   @ManyToOne
@JoinColumn(name = "owner_id", columnDefinition = "INTEGER")
private User owner;
}