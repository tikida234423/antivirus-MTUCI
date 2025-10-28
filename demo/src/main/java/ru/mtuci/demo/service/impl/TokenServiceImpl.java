package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.configuration.JwtTokenProvider;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.model.SessionStatus;
import ru.mtuci.demo.model.UserSession;
import ru.mtuci.demo.model.response.TokenResponse;
import ru.mtuci.demo.repository.UserRepository;
import ru.mtuci.demo.repository.UserSessionRepository;
import ru.mtuci.demo.service.TokenService;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final UserSessionRepository userSessionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public TokenResponse issueTokenPair(String email, Long deviceId, Set<GrantedAuthority> authorities) {

        String accessToken = jwtTokenProvider.generateAccessToken(email, authorities);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email, deviceId);

        Long now = System.currentTimeMillis();
        Date accessTokenExpiresAt = new Date(now + 1000 * 60 * 5);
        Date refreshTokenExpiresAt = new Date(now + 1000 * 60 * 60 * 24);

        UserSession newSession = new UserSession();
        newSession.setEmail(email);
        newSession.setDeviceId(deviceId);
        newSession.setAccessToken(accessToken);
        newSession.setRefreshToken(refreshToken);
        newSession.setAccessTokenExpiry(accessTokenExpiresAt);
        newSession.setRefreshTokenExpiry(refreshTokenExpiresAt);
        newSession.setStatus(SessionStatus.ACTIVE);

        userSessionRepository.save(newSession);

        return new TokenResponse(accessToken, refreshToken);
    }

    public void blockAllSessionsForUser(String email) {

        List<UserSession> sessions = userSessionRepository.findAllByEmail(email);

        for (UserSession session : sessions) {

            if (session.getStatus() == SessionStatus.ACTIVE) {

                session.setStatus(SessionStatus.REVOKED);

                userSessionRepository.save(session);

            }

        }

    }

    public TokenResponse refreshTokenPair(Long deviceId, String refreshToken) {

        UserSession session = userSessionRepository.findByRefreshToken(refreshToken).orElse(null);

        if (session == null ||
                session.getStatus() != SessionStatus.ACTIVE ||
                !Objects.equals(session.getDeviceId(), deviceId)) {

            blockAllSessionsForUser(session.getEmail());

            return null;

        }

        session.setStatus(SessionStatus.USED);

        userSessionRepository.save(session);

        ApplicationUser user = userRepository.findByEmail(session.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return issueTokenPair(session.getEmail(),
                                deviceId,
                                user.getRole().getGrantedAuthorities());

    }

}
