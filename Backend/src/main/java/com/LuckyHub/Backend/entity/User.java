package com.LuckyHub.Backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    private String password;

    private String firstName;
    private String lastName;

    private String avatarUrl;

    private boolean isVerified;

    private Date createdAt;
    private Date updatedAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    private Integer winnersSelectedThisMonth;

}
