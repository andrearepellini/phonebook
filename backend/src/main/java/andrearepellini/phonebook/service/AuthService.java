package andrearepellini.phonebook.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import andrearepellini.phonebook.dto.request.AuthenticateUserRequest;
import andrearepellini.phonebook.dto.request.RegisterUserRequest;
import andrearepellini.phonebook.dto.request.VerifyUserRequest;
import andrearepellini.phonebook.dto.response.UserResponse;
import andrearepellini.phonebook.entity.User;
import andrearepellini.phonebook.repository.UserRepository;
import jakarta.mail.MessagingException;

@Service
public class AuthService {
    private static final SecureRandom VERIFICATION_CODE_RANDOM = new SecureRandom();
    private static final int VERIFICATION_CODE_MAX_VALUE = 1_000_000;
    private static final long VERIFICATION_CODE_TTL_MINUTES = 10;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    private final static String EMAIL_SUBJECT = "Rubrica - Il tuo codice di verifica";

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @Transactional(rollbackFor = MessagingException.class)
    public UserResponse registerUser(RegisterUserRequest input) throws MessagingException {
        String normalizedEmail = normalizeEmail(input.email());

        if (userRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(input.password()));
        user.setVerified(false);

        String verificationCode = generateVerificationCode();
        user.setVerificationCodeHash(passwordEncoder.encode(verificationCode));
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(VERIFICATION_CODE_TTL_MINUTES));
        user = userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), EMAIL_SUBJECT, verificationCode);

        return toUserResponse(user);
    }

    @Transactional
    public UserResponse verifyUser(VerifyUserRequest input) {
        String normalizedEmail = normalizeEmail(input.email());

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email"));

        if (Boolean.TRUE.equals(user.getVerified())) {
            return toUserResponse(user);
        }

        if (user.getVerificationCodeHash() == null || user.getVerificationCodeExpiresAt() == null) {
            throw new IllegalArgumentException("Verification code not available");
        }

        if (LocalDateTime.now().isAfter(user.getVerificationCodeExpiresAt())) {
            throw new IllegalArgumentException("Verification code expired");
        }

        if (!passwordEncoder.matches(input.verificationCode(), user.getVerificationCodeHash())) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        user.setVerified(true);
        user.setVerificationCodeHash(null);
        user.setVerificationCodeExpiresAt(null);
        user = userRepository.save(user);

        return toUserResponse(user);
    }

    public String authenticateUser(AuthenticateUserRequest input) {
        String normalizedEmail = normalizeEmail(input.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizedEmail,
                        input.password()));

        Object principal = authentication.getPrincipal();
        String username = principal instanceof UserDetails
                ? ((UserDetails) principal).getUsername()
                : authentication.getName();

        return jwtService.generateToken(username);
    }

    public UserResponse getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found: " + authentication.getName()));

        return toUserResponse(user);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String generateVerificationCode() {
        return String.format("%06d", VERIFICATION_CODE_RANDOM.nextInt(VERIFICATION_CODE_MAX_VALUE));
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail());
    }

}
