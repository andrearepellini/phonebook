package andrearepellini.phonebook.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrearepellini.phonebook.dto.request.AuthenticateUserRequest;
import andrearepellini.phonebook.dto.request.RegisterUserRequest;
import andrearepellini.phonebook.dto.response.UserResponse;
import andrearepellini.phonebook.entity.User;
import andrearepellini.phonebook.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserResponse registerUser(RegisterUserRequest input) {
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user = userRepository.save(user);

        return new UserResponse(user.getId(), user.getEmail());
    }

    public String authenticateUser(AuthenticateUserRequest input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not registered"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()));

        return jwtService.generateToken(user.getEmail());
    }

    public UserResponse getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found: " + authentication.getName()));

        return new UserResponse(user.getId(), user.getEmail());
    }

}
