package com.ecommerceapp.userservice.services;

import com.ecommerceapp.userservice.models.*;
import com.ecommerceapp.userservice.repositories.RoleRepository;
import com.ecommerceapp.userservice.repositories.SessionRepository;
import com.ecommerceapp.userservice.repositories.UserRepository;
import exceptions.SessionLimitReachedException;
import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;
import exceptions.WrongPasswordException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private SecretKey key = Jwts.SIG.HS256.key().build();
    private SessionRepository sessionRepository;
    private RoleRepository roleRepository;
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, SessionRepository sessionRepository, RoleRepository roleRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.roleRepository = roleRepository;
    }
    public boolean signUp(String email, String password) throws UserAlreadyExistsException {
        if(userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
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
            String token = createSession(user.get());
            return token;
        }
        else{
            throw new WrongPasswordException("Email or password is incorrect");
        }

    }

    private String createJWTToken(Long user_id, Set<Role> roles, String email) {
        Map<String, Object> dataInJWT = new HashMap<>();
        dataInJWT.put("user_id", user_id);
        dataInJWT.put("roles", roles);
        dataInJWT.put("email", email);

        String token = Jwts.builder()
                .claims(dataInJWT)
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2)))
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(this.key)
                .compact();
        return token;

    }

    public Optional<Long> validate(String token) {
        try{
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            Date expiration = claims.getPayload().getExpiration();
            Long userId = claims.getPayload().get("user_id", Long.class);
            if(expiration.before(new Date())){
                return Optional.empty();
            }
            return Optional.of(userId);
        }catch (Exception e){
            return Optional.empty();
        }

    }

    @Transactional
    public String signInWithGoogle(String email)  {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if(existingUser.isPresent()) {
            String token = createSession(existingUser.get());
            return token;
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(null);
        user.setAuthProvider(AuthProvider.GOOGLE);
        userRepository.save(user);

        String token = createSession(user);
        return token;
    }

    public String createSession(User user){
        Long activeSessions = sessionRepository.countByUserAndStatus(user, SessionStatus.ACTIVE);
        if(activeSessions >= 2){
            throw new SessionLimitReachedException("You have reached the maximum number of active sessions");
        }
        String token = createJWTToken(user.getId(), user.getRoles(), user.getEmail());
        Session session = new Session();
        session.setUser(user);
        session.setStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setExpiryAt(new java.util.Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30))); //30 days
        sessionRepository.save(session);
        return token;

    }

    public boolean logout(String token) {
        Optional<Session> sessionOptional = sessionRepository.findByToken(token);
        if(sessionOptional.isPresent()){
            Session session = sessionOptional.get();
            if(session.getStatus() == SessionStatus.ENDED) {
                return true;
            }
            session.setStatus(SessionStatus.ENDED);
            sessionRepository.save(session);
            return true;
        }
        throw new IllegalArgumentException("Session not found or already logged out"); //ideally i should create a new exceptio but i am bored
    }

    public boolean logoutAllSessions(String token) {
        Optional<Session> sessionOptional = sessionRepository.findByToken(token);
        if (sessionOptional.isPresent()) {
            Session session = sessionOptional.get();
            User user = session.getUser();
            List<Session> sessions = sessionRepository.findAllByUserAndStatus(user, SessionStatus.ACTIVE);
            for (Session s : sessions) {
                s.setStatus(SessionStatus.ENDED);

            }
            sessionRepository.saveAll(sessions);
            return true;
        }
        throw new IllegalArgumentException("Session not found or already logged out"); //ideally i should create a new exceptio but i am bored
    }

    public boolean addRole(String Authorization, List<Role> roles ) {
        if(validate(Authorization).isEmpty()){
            throw new IllegalArgumentException("Invalid token");
        }
        Long userId = validate(Authorization).get();
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        List<Role> userRoles = new ArrayList<>();
        //move this logic so that DB update is done outside the loop later
        for(Role r : roles){
//
//            if(roleRepository.findByRole(r.getRole()).isEmpty()) {
//                roleRepository.save(r);
//            }

            Role managedRole = roleRepository.findByRole(r.getRole())
                    .orElseGet(() -> roleRepository.save(r));

           if(userOptional.get().getRoles().contains(managedRole)) {
               throw new IllegalArgumentException("Role already exists for the user");

           }
            userRoles.add(managedRole);
            userOptional.get().getRoles().add(managedRole);
        }
        userRepository.save(userOptional.get());
        return true;

    }


}
