package com.studyshare.server.model;

import com.studyshare.common.dto.BookDTO;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
@GeneratedValue(strategy = GenerationType.TABLE, generator = "book_generator")
@TableGenerator(
    name = "book_generator",
    table = "hibernate_sequences",
    pkColumnName = "sequence_name",
    valueColumnName = "next_val",
    pkColumnValue = "books",
    allocationSize = 1
)
@Column(name = "book_id")
private Long bookId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true)
    private String isbn;

    @Column(name = "available_copies")
    private Integer availableCopies;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;


    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private User borrower;

    private boolean available;
}
