package com.chatApplication.jwt;


import com.chatApplication.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Long userId=null;
        String jwtToken=null;

        String authHeader= request.getHeader("Authorization");

        if(authHeader!=null && authHeader.startsWith("Bearer ")){
            jwtToken=authHeader.substring(7);

            //IF JWT TOKEN IS NULL, NEED TO CHECK COOKIE

            if(jwtToken==null){
                Cookie []cookies=request.getCookies();
                if(cookies!=null){
                    for (Cookie cookie:cookies){
                        if("JWT".equals(cookie.getName())){
                            jwtToken=cookie.getValue();
                            break;
                        }
                    }
                }
            }

            if(jwtToken==null){
                filterChain.doFilter(request,response);
                return;
            }

            userId=jwtService.extractUserId(jwtToken);

            if(userId!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                var userDetails=userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));

                if (jwtService.isTokenValid(jwtToken,userDetails)){
                    UsernamePasswordAuthenticationToken authToken =new UsernamePasswordAuthenticationToken(userDetails,null, Collections.emptyList());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request,response);
            return;
        }
    }
}
