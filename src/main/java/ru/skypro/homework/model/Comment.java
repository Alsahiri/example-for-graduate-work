package ru.skypro.homework.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;

    @ManyToOne
    @JoinColumn(name = "ad_id", referencedColumnName = "ad_id")
    private Ad ad;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "user_id")
    private User author;

    @Column(name = "comment_text")
    private String commentText;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
