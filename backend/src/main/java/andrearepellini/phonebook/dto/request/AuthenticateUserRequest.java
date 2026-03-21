package andrearepellini.phonebook.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticateUserRequest(
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
        @NotBlank(message = "Password is required") String password) {

    @Override
    public String toString() {
        return "AuthenticateUserRequest[email=" + email + ", password=****]";
    }
}
