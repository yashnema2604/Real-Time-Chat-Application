package com.chatApplication.controller;


import com.chatApplication.dto.LoginRequestDTO;
import com.chatApplication.dto.LoginResponseDTO;
import com.chatApplication.dto.RegisterRequestDTO;
import com.chatApplication.dto.UserDTO;
import com.chatApplication.model.User;
import com.chatApplication.repository.UserRepository;
import com.chatApplication.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody RegisterRequestDTO registerRequestDTO){
        return ResponseEntity.ok(authenticationService.signup(registerRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
       LoginResponseDTO loginResponseDTO=authenticationService.login(loginRequestDTO);
        ResponseCookie responseCookie=ResponseCookie.from("JWT",loginResponseDTO.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(1*60*60) //1 HOUR
                .sameSite("strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body(loginResponseDTO.getUserDTO());
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(){
        return authenticationService.logout();
    }

    @GetMapping("/getonlineusers")
    public ResponseEntity<Map<String,Object>> getOnlineUsers(){
        return ResponseEntity.ok(authenticationService.getOnlineUsers());
    }

    @GetMapping("/getcurrentuser")
    public ResponseEntity<?> getCurrentUser(Authentication authentication){
        if(authentication==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("USER NOT AUTHORIZED");
        }

        String username=authentication.getName();
        User user=userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("USER NOT FOUND"));

        return ResponseEntity.ok(convertToUserDTO(user));
    }

    public UserDTO convertToUserDTO(User user){
        UserDTO userDTO=new UserDTO();
        userDTO.setUseremail(user.getUseremail());
        userDTO.setUsername(user.getUsername());

        return userDTO;
    }
}
