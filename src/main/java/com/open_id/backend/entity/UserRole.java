package com.open_id.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_role")
public class UserRole {

    @Id
    private Integer id;

    private String name;

    private String description;

    @OneToOne(mappedBy = "role")
    private AppUser user;
}
