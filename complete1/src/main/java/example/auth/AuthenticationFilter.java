package example.auth;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@AllArgsConstructor
class AuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_HEADER = "token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        TokenAuthentication token = authenticate(request.getHeader(TOKEN_HEADER));
        addTokenResponseHeader(token, response);
        SecurityContextHolder.getContext().setAuthentication(token);
        chain.doFilter(request, response);
    }

    private TokenAuthentication authenticate(String token) {
        return StringUtils.hasText(token) ? new TokenAuthentication(token) : null;
    }

    private void addTokenResponseHeader(TokenAuthentication token, HttpServletResponse response) {
        if (token != null && StringUtils.hasText(token.getToken())) {
            response.setHeader(TOKEN_HEADER, token.getToken());
        }
    }

}
