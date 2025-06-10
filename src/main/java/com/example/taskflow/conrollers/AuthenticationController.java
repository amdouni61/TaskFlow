package com.example.taskflow.conrollers;

import com.example.taskflow.dtos.LoginUserDto;
import com.example.taskflow.dtos.RegisterUserDto;
import com.example.taskflow.exceptions.InvalidCredentialsException;
import com.example.taskflow.exceptions.UserAlreadyExistsException;
import com.example.taskflow.model.User;
import com.example.taskflow.responses.ErrorResponse;
import com.example.taskflow.responses.LoginResponse;
import com.example.taskflow.service.JwtService;
import com.example.taskflow.service.imp.AuthServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthServiceImpl authenticationService;

    public AuthenticationController(JwtService jwtService, AuthServiceImpl authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        try {
            User registeredUser = authenticationService.signup(registerUserDto);
            return ResponseEntity.ok(registeredUser); // Return registered user on success
        } catch (UserAlreadyExistsException ex) {
            ErrorResponse errorResponse = new ErrorResponse("USER_ALREADY_EXISTS", "Email is already in use");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("INTERNAL_ERROR", "An error occurred during registration");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto) {
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);

            String jwtToken = jwtService.generateToken(authenticatedUser);

            LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse); // Return login response on success
        } catch (InvalidCredentialsException ex) {
            ErrorResponse errorResponse = new ErrorResponse("INVALID_CREDENTIALS", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("INTERNAL_ERROR", "An error occurred during authentication");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
