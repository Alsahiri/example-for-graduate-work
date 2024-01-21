package ru.skypro.homework.model;

import lombok.Data;

import javax.persistence.*;
@Data
@Entity
@Table(name = "ads")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ad_id")
    private Integer adId;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "user_id")
    private User author;

    @Column(name = "ad_price", nullable = false)
    private Integer price;

    @Column(name = "ad_title", nullable = false)
    private String title;

    @Column(name = "ad_description")
    private String description;

    @Column(name = "image_path")
    private String image;
}
