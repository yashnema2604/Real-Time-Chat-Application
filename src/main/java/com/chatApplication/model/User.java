package com.chatApplication.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false ,unique = true)
    private String useremail;

    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,name = "is_online")
    private boolean isOnline;
}
