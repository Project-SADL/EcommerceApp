package com.ecommerceapp.userservice.controllers;

import com.ecommerceapp.userservice.dtos.*;
import com.ecommerceapp.userservice.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<StandardResponseDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        StandardResponseDto StandardResponseDto = new StandardResponseDto();
        try{
            if(authService.signUp(signUpRequestDto.getEmail(), signUpRequestDto.getPassword())){
                StandardResponseDto.setRequestStatus(RequestStatus.SUCCESS);
            } else {
                StandardResponseDto.setRequestStatus(RequestStatus.FAILURE);
            }
            return new ResponseEntity<>(StandardResponseDto, HttpStatus.OK);
        } catch (Exception e) {
            StandardResponseDto.setRequestStatus(RequestStatus.FAILURE);
            return new ResponseEntity<>(StandardResponseDto, HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        StandardResponseDto StandardResponseDto = new StandardResponseDto();
        try{
            String token = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

            if(token != null){
                StandardResponseDto.setRequestStatus(RequestStatus.SUCCESS);
                headers.add("Authorization Token", token);
            } else {

                StandardResponseDto.setRequestStatus(RequestStatus.FAILURE);
            }
            ResponseEntity<StandardResponseDto> response = new ResponseEntity<>(
                    StandardResponseDto, headers, HttpStatus.OK);

            return response;
        }catch (Exception e){
            StandardResponseDto.setRequestStatus(RequestStatus.FAILURE);
            return new ResponseEntity<StandardResponseDto>(StandardResponseDto, HttpStatus.UNAUTHORIZED);
        }

    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam String token) {
       if(authService.validate(token).isEmpty()) return false;
       return true;
    }

    @PostMapping("/logout")
    public ResponseEntity<StandardResponseDto> logout(@RequestHeader String Authorization) {
        StandardResponseDto standardResponseDto = new StandardResponseDto();
        try{
            authService.logout(Authorization);
            standardResponseDto.setRequestStatus(RequestStatus.SUCCESS);
            standardResponseDto.setMessage("Logged out successfully");
            return new ResponseEntity<>(standardResponseDto, HttpStatus.OK);
        }
        catch (Exception e){
            standardResponseDto.setRequestStatus(RequestStatus.FAILURE);
            standardResponseDto.setMessage(e.getMessage());

        }
        return new ResponseEntity<>(standardResponseDto, HttpStatus.UNAUTHORIZED);

    }

    @PostMapping("/logout-all")
    public ResponseEntity<StandardResponseDto> logoutAllSessions(@RequestHeader String Authorization) {
        StandardResponseDto standardResponseDto = new StandardResponseDto();
        try{
            authService.logoutAllSessions(Authorization);
            standardResponseDto.setRequestStatus(RequestStatus.SUCCESS);
            standardResponseDto.setMessage("Logged out from all sessions successfully");
            return new ResponseEntity<>(standardResponseDto, HttpStatus.OK);
        }
        catch (Exception e){
            standardResponseDto.setRequestStatus(RequestStatus.FAILURE);
            standardResponseDto.setMessage(e.getMessage());

        }
        return new ResponseEntity<>(standardResponseDto, HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/add-role")
    public ResponseEntity<StandardResponseDto> addRole(@RequestHeader String authorization, @RequestHeader String Secret_Key, @RequestBody AddRoleRequestDto addRoleRequestDto ) {
        StandardResponseDto standardResponseDto = new StandardResponseDto();
        if(!Secret_Key.equals("X")){
           standardResponseDto.setRequestStatus(RequestStatus.FAILURE);
            standardResponseDto.setMessage("Unauthorized");
            return new ResponseEntity<>(standardResponseDto, HttpStatus.UNAUTHORIZED);
        }
        try{
            authService.addRole(authorization,addRoleRequestDto.getRoles());
            standardResponseDto.setRequestStatus(RequestStatus.SUCCESS);
            standardResponseDto.setMessage("Roles added successfully");
            return new ResponseEntity<>(standardResponseDto, HttpStatus.OK);
        }catch (Exception e){
            standardResponseDto.setRequestStatus(RequestStatus.FAILURE);
            standardResponseDto.setMessage(e.getMessage());
            return new ResponseEntity<>(standardResponseDto, HttpStatus.UNAUTHORIZED);
        }




    }


//    @GetMapping("/signup-google")
//    public StandardResponseDto signUpWithGoogle(@AuthenticationPrincipal OAuth2User oAuth2User){
//        StandardResponseDto StandardResponseDto = new StandardResponseDto();
//        try{
//            String email = oAuth2User.getAttribute("email");
//            if(authService.signUpWithGoogle(email)){
//                StandardResponseDto.setRequestStatus(RequestStatus.SUCCESS);
//            } else {
//                StandardResponseDto.setRequestStatus(RequestStatus.FAILURE);
//            }
//            return StandardResponseDto;
//        } catch (Exception e) {
//            StandardResponseDto.setRequestStatus(RequestStatus.FAILURE);
//            return StandardResponseDto;
//        }
//    }

}
