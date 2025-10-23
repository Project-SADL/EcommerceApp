package com.ecommerceapp.userservice.controllers;

import com.ecommerceapp.userservice.dtos.*;
import com.ecommerceapp.userservice.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        SignUpResponseDto signUpResponseDto = new SignUpResponseDto();
        try{
            if(authService.signUp(signUpRequestDto.getEmail(), signUpRequestDto.getPassword())){
                signUpResponseDto.setRequestStatus(RequestStatus.SUCCESS);
            } else {
                signUpResponseDto.setRequestStatus(RequestStatus.FAILURE);
            }
            return new ResponseEntity<>(signUpResponseDto, HttpStatus.OK);
        } catch (Exception e) {
            signUpResponseDto.setRequestStatus(RequestStatus.FAILURE);
            return new ResponseEntity<>(signUpResponseDto, HttpStatus.CONFLICT);
        }


    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        try{
            String token = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

            if(token != null){
                loginResponseDto.setRequestStatus(RequestStatus.SUCCESS);
                headers.add("Authorization Token", token);
            } else {

                loginResponseDto.setRequestStatus(RequestStatus.FAILURE);
            }
            ResponseEntity<LoginResponseDto> response = new ResponseEntity<>(
                    loginResponseDto, headers, HttpStatus.OK);

            return response;
        }catch (Exception e){
            loginResponseDto.setRequestStatus(RequestStatus.FAILURE);
            return new ResponseEntity<LoginResponseDto>(loginResponseDto, HttpStatus.UNAUTHORIZED);
        }

    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam String token) {
       return authService.validate(token);
    }
}
