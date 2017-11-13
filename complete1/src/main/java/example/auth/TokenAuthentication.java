package example.auth;

import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.util.Args;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class TokenAuthentication implements Authentication {

    public static final GrantedAuthority AUTENTICATED = new SimpleGrantedAuthority("ROLE_AUTHENTICATED");

    @Getter
    private final String token;

    @Getter
    @Setter
    private boolean authenticated;

    TokenAuthentication(String aToken) {
        Args.notBlank(aToken, "Token");
        this.token = aToken;
        this.authenticated = true;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(AUTENTICATED);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return token;
    }

}
