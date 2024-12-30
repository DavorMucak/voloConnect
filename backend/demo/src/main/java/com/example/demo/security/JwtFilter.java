package com.example.demo.security;

import org.springframework.context.ApplicationContext;
import com.example.demo.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.example.demo.service.JwtService;
import com.example.demo.service.MyUserDetailsService;
import com.example.demo.service.CustomOAuth2UserService;


@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private MyUserDetailsService userDetailService;
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;
    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        String authenticationHeader = request.getHeader("Authorization");
        if (authenticationHeader != null && authenticationHeader.startsWith("Bearer ")){
            token = authenticationHeader.substring(7);
        }
        if (token != null && !token.isEmpty()) {
            try {
                if(isJwtToken(token) && !isOAuth2Token(token)){
                    handleJwtToken(token, request);
                } else if (isOAuth2Token(token)){
                    handleOAuth2Token(token, request);
                }
            } catch (GeneralSecurityException e) {
                /*
                if (isJwtToken(token)) {
                    handleJwtToken(token, request);
                } else {
                    throw new RuntimeException(e);
                }
                 */
                e.printStackTrace();
            }
        }
        filterChain.doFilter(request, response);
    }

    //Google ID token pocinje znakovima eyJ i mora sadržavati točku
    private boolean isOAuth2Token(String token) {
        return token.startsWith("eyJ") && token.contains(".");
    }

    //jwt token mora imati 3 dijela
    private boolean isJwtToken(String token) {
        if (token.split("\\.").length == 3) {
            return true;
        }
        return false;
    }


    private void handleOAuth2Token(String token, HttpServletRequest request) throws GeneralSecurityException, IOException {
        OAuth2User oAuth2User = customOAuth2UserService.verifyOAuth2Token(token);
        if(oAuth2User != null){
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            throw new GeneralSecurityException("OAuth2 Google token verification failed");
        }

    }


    private void handleJwtToken(String token, HttpServletRequest request) {
        String username = jwtService.extractUserName(token);
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
    }


}
