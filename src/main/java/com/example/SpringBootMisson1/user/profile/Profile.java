package com.example.SpringBootMisson1.user.profile;

import com.example.SpringBootMisson1.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String intro;

    @OneToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private SiteUser author;
}