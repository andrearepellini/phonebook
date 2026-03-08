package andrearepellini.phonebook.service;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthCookieService {

    @Value("${security.jwt.cookie.name:phonebook_auth}")
    private String authCookieName;

    @Value("${security.jwt.cookie.path:/}")
    private String authCookiePath;

    @Value("${security.jwt.cookie.same-site:Strict}")
    private String authCookieSameSite;

    @Value("${security.jwt.cookie.secure:true}")
    private boolean authCookieSecure;

    private final JwtService jwtService;

    public AuthCookieService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public long getExpirationTime() {
        return jwtService.getExpirationTime();
    }

    public ResponseCookie buildAuthCookie(String token) {
        return ResponseCookie.from(authCookieName, token)
                .httpOnly(true)
                .secure(authCookieSecure)
                .path(authCookiePath)
                .sameSite(authCookieSameSite)
                .maxAge(Duration.ofMillis(jwtService.getExpirationTime()))
                .build();
    }

    public ResponseCookie buildLogoutCookie() {
        return ResponseCookie.from(authCookieName, "")
                .httpOnly(true)
                .secure(authCookieSecure)
                .path(authCookiePath)
                .sameSite(authCookieSameSite)
                .maxAge(Duration.ZERO)
                .build();
    }

    public String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> authCookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
