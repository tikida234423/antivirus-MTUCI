package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.model.UserDetailsImpl;
import ru.mtuci.demo.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        ApplicationUser applicationUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(("User not found")));

        return UserDetailsImpl.fromApplicationUser(applicationUser);

    }

    public ApplicationUser getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(("User not found")));
    }

    public ApplicationUser getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(("User not found")));
    }

    public void saveUser(ApplicationUser applicationUser) {
        userRepository.save(applicationUser);
    }

    public ApplicationUser updateUser(ApplicationUser applicationUser) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        ApplicationUser user = getUserByEmail(applicationUser.getEmail());
        user.setUsername(applicationUser.getUsername());
        user.setPassword(passwordEncoder.encode(applicationUser.getPassword()));

        return userRepository.save(user);

    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
