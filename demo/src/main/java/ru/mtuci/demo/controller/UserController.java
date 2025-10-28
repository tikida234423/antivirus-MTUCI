package ru.mtuci.demo.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.demo.configuration.JwtTokenFilter;
import ru.mtuci.demo.configuration.JwtTokenProvider;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.model.Role;
import ru.mtuci.demo.model.request.UserDeleteRequest;
import ru.mtuci.demo.model.request.UserUpdateRequest;
import ru.mtuci.demo.model.response.ProfileGetResponse;
import ru.mtuci.demo.model.response.UserDeleteResponse;
import ru.mtuci.demo.model.response.UserUpdateResponse;
import ru.mtuci.demo.service.impl.UserDetailsServiceImpl;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenFilter jwtTokenFilter;

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile(@RequestHeader("Authorization") String authHeader) {

        ProfileGetResponse response = new ProfileGetResponse();

        try {

            authHeader = authHeader.replace("Bearer ", "");

            String email = jwtTokenProvider.getUsername(authHeader);
            ApplicationUser user = userDetailsService.getUserByEmail(email);

            response.setId(user.getId());
            response.setEmail(user.getEmail());
            response.setUsername(user.getUsername());
            response.setRole(user.getRole());
            response.setStatus("Ok");

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }

    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest request,
                                        @RequestHeader("Authorization") String authHeader) {

        UserUpdateResponse response = new UserUpdateResponse();

        try {

            authHeader = authHeader.replace("Bearer ", "");

            ApplicationUser user = userDetailsService.getUserByEmail(
                    jwtTokenProvider.getUsername(authHeader)
            );

            if (user == null) {

                response.setStatus("User not found");

                return ResponseEntity.badRequest()
                        .body(response);

            }

            String email = request.getEmail();

            if (!user.getEmail().equals(email) && user.getRole() != Role.ADMIN) {

                response.setStatus("User not authorized");

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(response);
            }

            ApplicationUser newUser = new ApplicationUser();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setRole(request.getRole());
            newUser.setPassword(request.getPassword());

            ApplicationUser updatedUser = userDetailsService.updateUser(newUser);

            response.setId(updatedUser.getId());
            response.setStatus("Ok");

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }

    }

    @DeleteMapping("/delete")
    @Transactional
    public ResponseEntity<?> deleteUser(@RequestBody UserDeleteRequest request,
                                        @RequestHeader("Authorization") String authHeader) {

        UserDeleteResponse response = new UserDeleteResponse();

        try {

            authHeader = authHeader.replace("Bearer ", "");

            ApplicationUser user = userDetailsService.getUserByEmail(request.getEmail());

            if (user == null) {

                response.setStatus("User not found");

                return ResponseEntity.badRequest()
                        .body(response);

            }

            ApplicationUser curUser = userDetailsService.getUserByEmail(jwtTokenProvider.getUsername(authHeader));

            if (curUser != user && curUser.getRole() != Role.ADMIN) {

                response.setStatus("User not authorized");

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(response);

            }

            Long id = user.getId();

            userDetailsService.deleteUser(id);

            response.setId(id);
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
