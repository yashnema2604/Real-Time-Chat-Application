package com.chatApplication.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserDTO {


    private Long Id;

    @Column(nullable = false ,unique = true)
    private String useremail;

    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false,name = "is_online")
    private boolean isOnline;
}
