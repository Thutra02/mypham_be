package datn.com.cosmetics.bean.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email
    @NotBlank
    String email;

    @NotBlank
    @Size(min = 6, max = 20)
    String password;

    @NotBlank
    @Size(min = 3, max = 15)
    String username;
}
