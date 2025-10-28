package ru.mtuci.demo.controller;


import lombok.AllArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.model.Role;
import ru.mtuci.demo.model.request.LoginRequest;
import ru.mtuci.demo.model.request.TokenRefreshRequest;
import ru.mtuci.demo.model.response.TokenRefreshResponse;
import ru.mtuci.demo.model.response.TokenResponse;
import ru.mtuci.demo.model.response.UserLoginResponse;
import ru.mtuci.demo.model.response.UserRegisterResponse;
import ru.mtuci.demo.repository.UserRepository;
import ru.mtuci.demo.service.TokenService;
import ru.mtuci.demo.service.impl.UserDetailsServiceImpl;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody ApplicationUser applicationUser) {

        UserRegisterResponse response = new UserRegisterResponse();

        try {

            if (userRepository.findByEmail(applicationUser.getEmail()).isPresent()) {

                response.setStatus("User already exists");

                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(response);

            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            applicationUser.setEmail(applicationUser.getEmail());
            applicationUser.setUsername(applicationUser.getUsername());
            applicationUser.setPassword(encoder.encode(applicationUser.getPassword()));
            applicationUser.setRole(Role.USER);

            userDetailsServiceImpl.saveUser(applicationUser);

            response.setId(applicationUser.getId());
            response.setStatus("Ok");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);
        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        UserLoginResponse response = new UserLoginResponse();

        try {

            String email = loginRequest.getEmail();
            Optional<ApplicationUser> applicationUser = userRepository.findByEmail(email);

            if (applicationUser.isEmpty())
            {
                response.setStatus("User not found");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(response);
            }

            authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    loginRequest.getPassword()
                            )
                    );

            TokenResponse tokenResponse = tokenService.issueTokenPair(
                    email,
                    loginRequest.getDeviceId(),
                    applicationUser.get().getRole().getGrantedAuthorities()
            );

            response.setEmail(email);
            response.setTokens(tokenResponse);
            response.setUsername(applicationUser.get().getUsername());
            response.setStatus("Ok");

            return ResponseEntity.ok(response);
        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenRefreshRequest request) {

        TokenRefreshResponse response = new TokenRefreshResponse();

        try {

            TokenResponse tokenResponse = tokenService.refreshTokenPair(request.getDeviceId(),
                                                                    request.getRefreshToken());

            if (tokenResponse == null) {

                response.setStatus("Invalid refresh token or device id");

                return ResponseEntity.badRequest()
                        .body(response);

            }

            response.setRefreshToken(tokenResponse.getRefreshToken());
            response.setAccessToken(tokenResponse.getAccessToken());
            response.setStatus("Ok");

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }

    }

}
