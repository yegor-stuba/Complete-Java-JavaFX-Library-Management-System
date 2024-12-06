package com.studyshare.server.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "book_id", columnDefinition = "INTEGER")
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