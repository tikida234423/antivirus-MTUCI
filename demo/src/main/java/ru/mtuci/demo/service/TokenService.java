package ru.mtuci.demo.service;

import org.springframework.security.core.GrantedAuthority;
import ru.mtuci.demo.model.response.TokenResponse;

import java.util.Set;

public interface TokenService {

    TokenResponse issueTokenPair(String email, Long deviceId, Set<GrantedAuthority> authorities);

    TokenResponse refreshTokenPair(Long deviceId, String refreshToken);

}
