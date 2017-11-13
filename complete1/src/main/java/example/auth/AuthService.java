package example.auth;

import lombok.AllArgsConstructor;
import org.apache.http.util.Args;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private static final String INVALID_MESSAGE = "Invalid credentials";

    public String login(String username, String password) {
        Args.notBlank(username, "User name");
        Args.notBlank(password, "Password");
        if ("user".equals(username) && "pass".equals(password)) {
            return "anytoken";
        } else {
            throw new AccessDeniedException(INVALID_MESSAGE);
        }
    }

    public boolean validate(String token) {
        Args.notBlank(token, "Token");
        return true;
    }

    public void logout(String token) {
        Args.notBlank(token, "Token");
    }

}
