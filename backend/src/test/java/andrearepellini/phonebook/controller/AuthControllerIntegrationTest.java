package andrearepellini.phonebook.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.mockito.ArgumentCaptor;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;

import andrearepellini.phonebook.entity.User;
import andrearepellini.phonebook.repository.UserRepository;
import andrearepellini.phonebook.service.EmailService;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private EmailService emailService;

    @Value("${security.jwt.cookie.name:phonebook_auth}")
    private String authCookieName;

    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        saveUser("user@example.com", "Password123!", true);
    }

    @Test
    @DisplayName("Signup normalizes email casing before saving")
    void signupNormalizesEmailBeforePersisting() throws Exception {
        userRepository.deleteAll();

        mockMvc.perform(post("/api/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "NewUser@Example.COM",
                          "password": "Password123!"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@example.com"));

        User savedUser = userRepository.findByEmailIgnoreCase("newuser@example.com").orElseThrow();
        assertEquals("newuser@example.com", savedUser.getEmail());
        assertEquals(false, savedUser.getVerified());
    }

    @Test
    @DisplayName("Signup rejects duplicate emails regardless of case")
    void signupRejectsDuplicateEmailRegardlessOfCase() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "USER@EXAMPLE.COM",
                          "password": "AnotherPassword123!"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already registered"));
    }

    @Test
    @DisplayName("Login succeeds with normalized email and sets auth cookie")
    void loginWithDifferentEmailCaseSucceedsAndSetsAuthCookie() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "USER@EXAMPLE.COM",
                          "password": "Password123!"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expiresIn").value(3600000L))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString(authCookieName + "=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")));
    }

    @Test
    void loginWithUnknownEmailReturnsGenericUnauthorizedResponse() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "missing@example.com",
                          "password": "Password123!"
                        }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void loginWithWrongPasswordReturnsSameGenericUnauthorizedResponse() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "user@example.com",
                          "password": "WrongPassword123!"
                        }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    @DisplayName("Login rejects users whose email is not verified")
    void loginRejectsUnverifiedUsers() throws Exception {
        userRepository.deleteAll();
        saveUser("pending@example.com", "Password123!", false);

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "pending@example.com",
                          "password": "Password123!"
                        }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    @DisplayName("Verify activates the user when the six-digit code matches")
    void verifyActivatesUserWhenCodeMatches() throws Exception {
        userRepository.deleteAll();

        mockMvc.perform(post("/api/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "NewUser@Example.COM",
                          "password": "Password123!"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@example.com"));

        ArgumentCaptor<String> verificationCodeCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendVerificationEmail(
                eq("newuser@example.com"),
                eq("Rubrica - Il tuo codice di verifica"),
                verificationCodeCaptor.capture());

        String verificationCode = verificationCodeCaptor.getValue();
        assertEquals(6, verificationCode.length());
        assertTrue(verificationCode.chars().allMatch(Character::isDigit));

        mockMvc.perform(post("/api/auth/verify")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "newuser@example.com",
                          "verificationCode": "%s"
                        }
                        """.formatted(verificationCode)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@example.com"));

        User savedUser = userRepository.findByEmailIgnoreCase("newuser@example.com").orElseThrow();
        assertEquals(true, savedUser.getVerified());
        assertNull(savedUser.getVerificationCodeHash());
        assertNull(savedUser.getVerificationCodeExpiresAt());
    }

    @Test
    @DisplayName("Verify rejects an invalid code and keeps the user unverified")
    void verifyRejectsInvalidCode() throws Exception {
        userRepository.deleteAll();

        mockMvc.perform(post("/api/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "pending@example.com",
                          "password": "Password123!"
                        }
                        """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/verify")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "pending@example.com",
                          "verificationCode": "000000"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid verification code"));

        User savedUser = userRepository.findByEmailIgnoreCase("pending@example.com").orElseThrow();
        assertEquals(false, savedUser.getVerified());
        assertTrue(savedUser.getVerificationCodeHash() != null && !savedUser.getVerificationCodeHash().isBlank());
    }

    @Test
    @DisplayName("Authenticated me endpoint returns current user from auth cookie")
    void meReturnsAuthenticatedUserFromAuthCookie() throws Exception {
        Cookie authCookie = loginAndExtractAuthCookie("USER@EXAMPLE.COM", "Password123!");

        mockMvc.perform(get("/api/auth/me")
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    @DisplayName("Expired JWT is rejected with generic unauthorized response")
    void meWithExpiredJwtReturnsUnauthorized() throws Exception {
        String expiredToken = Jwts.builder()
                .subject("user@example.com")
                .issuedAt(new Date(System.currentTimeMillis() - 10_000))
                .expiration(new Date(System.currentTimeMillis() - 1_000))
                .signWith(Keys.hmacShaKeyFor(jwtSecretKey.getBytes()))
                .compact();

        mockMvc.perform(get("/api/auth/me")
                .cookie(new Cookie(authCookieName, expiredToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    @DisplayName("Logout clears the auth cookie")
    void logoutClearsAuthCookie() throws Exception {
        Cookie authCookie = loginAndExtractAuthCookie("user@example.com", "Password123!");

        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .cookie(authCookie))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString(authCookieName + "=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));
    }

    private void saveUser(String email, String password, boolean verified) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setVerified(verified);
        userRepository.save(user);
    }

    private Cookie loginAndExtractAuthCookie(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "%s",
                          "password": "%s"
                        }
                        """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        String setCookieHeader = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertTrue(setCookieHeader != null && !setCookieHeader.isBlank());

        String[] cookieParts = setCookieHeader.split(";", 2)[0].split("=", 2);
        return new Cookie(cookieParts[0], cookieParts[1]);
    }
}
