package com.example.demo.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.context.ApplicationContext;
import com.example.demo.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.service.JwtService;
import com.example.demo.service.MyUserDetailsService;
import com.example.demo.service.CustomOAuth2UserService;

//Security filter za komunikaciju tokena
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
        //System.out.println("Authorization header: " + authenticationHeader);
        if (authenticationHeader != null && authenticationHeader.startsWith("Bearer ")){
            token = authenticationHeader.substring(7);
        }
        if (token != null && !token.isEmpty()) {
            try {
                if(isGoogleToken(token)){
                    handleOAuth2Token(token, request);
                    return;
                } else {
                    handleJwtToken(token, request);
                }
            }
            catch (Exception e) {

                if (token.startsWith("ey")) {
                    handleJwtToken(token, request);
                } else {
                    throw new RuntimeException(e);
                }

                e.printStackTrace();
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isJwtToken(String token) {
        if(token.startsWith("<CustomJWT>"))
            return true;
        return false;
    }
    //koristim ugradene metode za dekodiranje tokena da provjerim issuera
    // issuer == "accounts.google.com" ==> google oauth2 token
    private boolean isGoogleToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        String issuer = decodedJWT.getIssuer();
        return issuer != null && issuer.contains("accounts.google.com");
    }

    private void handleOAuth2Token(String token, HttpServletRequest request) throws GeneralSecurityException, IOException {
        if(token.startsWith("<GoogleJWT>"))
            token = token.substring("<GoogleJWT>".length());
        //autenticiraj
        OAuth2User oAuth2User = customOAuth2UserService.verifyOAuth2Token(token, "");
        if(oAuth2User != null){
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            throw new GeneralSecurityException("OAuth2 Google token verification failed");
        }
    }

    //ako nije Google token, onda je obican token
    private void handleJwtToken(String token, HttpServletRequest request) {
        if(token.startsWith("<CustomJWT>"))
            token = token.substring("<CustomJWT>".length());
        System.out.println("token " + token);
        //izvuci informacije za prijavu iz tokena
        String username = jwtService.extractUserName(token);
        String role = jwtService.extractRole(token);
        String formattedRole = "ROLE_" + role.toUpperCase();
        //autenticiraj
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)){
                Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(formattedRole));
                System.out.println("Autoritet: " + authorities);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("Role korisnika na kraju baš: " +  SecurityContextHolder.getContext().getAuthentication());
            }
        }
    }


}

