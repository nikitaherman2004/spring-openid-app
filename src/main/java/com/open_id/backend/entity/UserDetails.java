package com.open_id.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_details")
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;

    private String email;

    private String picture;

    private String aboutYourself;

    private LocalDateTime dateOfBirth;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "sub")
    private AppUser user;
}
