package com.ecommerceapp.userservice.services;

import com.ecommerceapp.userservice.models.Role;
import com.ecommerceapp.userservice.models.Session;
import com.ecommerceapp.userservice.models.SessionStatus;
import com.ecommerceapp.userservice.models.User;
import com.ecommerceapp.userservice.repositories.SessionRepository;
import com.ecommerceapp.userservice.repositories.UserRepository;
import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;
import exceptions.WrongPasswordException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private SecretKey key = Jwts.SIG.HS256.key().build();
    private SessionRepository sessionRepository;
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, SessionRepository sessionRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }
    public boolean signUp(String email, String password) throws UserAlreadyExistsException {
        if(userRepository.findByEmail(email) != null) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);

        return true;
    }

    public String login(String email, String password) throws UserNotFoundException, WrongPasswordException {
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()){
            throw new UserNotFoundException("User not found");
        }

        boolean matches = bCryptPasswordEncoder.matches(password, user.get().getPassword());
        if(matches){
            String token = createJWTToken(user.get().getId(), new ArrayList<>(), user.get().getEmail());
            Session session = new Session();
            session.setUser(user.get());
            session.setStatus(SessionStatus.ACTIVE);
            session.setToken(token);
            session.setExpiryAt(new java.util.Date(System.currentTimeMillis() + 86400000)); //change this to 2 days or 30 days?
            sessionRepository.save(session);
            return token;
        }
        else{
            throw new WrongPasswordException("Email or password is incorrect");
        }

    }

    private String createJWTToken(Long user_id, List<Role> roles, String email) {
        Map<String, Object> dataInJWT = new HashMap<>();
        dataInJWT.put("user_id", user_id);
        dataInJWT.put("roles", roles);
        dataInJWT.put("email", email);

        String token = Jwts.builder()
                .claims(dataInJWT)
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(this.key)
                .compact();// 1 day expi

        return token;

    }

    public boolean validate(String token) {
        try{
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            Date expiration = claims.getPayload().getExpiration();
            Long userId = claims.getPayload().get("user_id", Long.class);
            return true;
        }catch (Exception e){
            return false;
        }

    }

}
