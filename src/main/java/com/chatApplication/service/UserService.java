package com.chatApplication.service;


import com.chatApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean userExist(String username){
        return userRepository.existsByUsernane(username);
    }

    public void setUserOnlineStatus(String username,boolean isOnline){
        userRepository.updateUserOnlineStatus(username,isOnline);
    }
}
