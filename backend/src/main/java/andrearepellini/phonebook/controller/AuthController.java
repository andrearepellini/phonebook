package andrearepellini.phonebook.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import andrearepellini.phonebook.dto.request.AuthenticateUserRequest;
import andrearepellini.phonebook.dto.request.RegisterUserRequest;
import andrearepellini.phonebook.dto.request.VerifyUserRequest;
import andrearepellini.phonebook.dto.response.LoginResponse;
import andrearepellini.phonebook.dto.response.UserResponse;
import andrearepellini.phonebook.service.AuthCookieService;
import andrearepellini.phonebook.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "User authentication APIs")
public class AuthController {
    private final AuthService authService;
    private final AuthCookieService authCookieService;

    public AuthController(
            AuthService authService,
            AuthCookieService authCookieService) {
        this.authService = authService;
        this.authCookieService = authCookieService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided registration details")
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already registered", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegisterUserRequest request) throws MessagingException {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @Operation(summary = "Verify user and confirm registration", description = "Verifies a user account and enables it")
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully verified"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/verify")
    public ResponseEntity<UserResponse> verifyUser(@Valid @RequestBody VerifyUserRequest request) {
        return ResponseEntity.ok(authService.verifyUser(request));
    }

    @Operation(summary = "Authenticate a user", description = "Authenticates a user and sets a secure HttpOnly authentication cookie")
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user not registered", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody AuthenticateUserRequest request) {
        String token = authService.authenticateUser(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookieService.buildAuthCookie(token).toString())
                .body(new LoginResponse(authCookieService.getExpirationTime()));
    }

    @Operation(summary = "Get authenticated user", description = "Returns the currently authenticated user")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.getAuthenticatedUser(authentication));
    }

    @Operation(summary = "Logout", description = "Clears the authentication cookie")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authCookieService.buildLogoutCookie().toString())
                .build();
    }

    @Operation(summary = "Get CSRF token", description = "Returns the CSRF token required for state-changing requests")
    @SecurityRequirements
    @GetMapping("/csrf")
    public ResponseEntity<Map<String, String>> csrf(@Parameter(hidden = true) CsrfToken csrfToken) {
        return ResponseEntity.ok(Map.of("token", csrfToken.getToken()));
    }
}
