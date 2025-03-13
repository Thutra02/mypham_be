package datn.com.cosmetics.bean.response;

import datn.com.cosmetics.entity.User;
import lombok.Data;

@Data
public class LoginResponse {
    User user;
    String token;

    public LoginResponse(User user, String token) {
        this.user = user;
        this.token = token;
    }
}
