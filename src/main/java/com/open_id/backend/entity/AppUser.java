package com.open_id.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Data
@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    private String sub;

    private String name;

    private String givenName;

    private String familyName;

    @Fetch(value = FetchMode.JOIN)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_role_id", referencedColumnName = "id")
    private UserRole role;

    @Fetch(value = FetchMode.JOIN)
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private UserDetails userDetails;
}
