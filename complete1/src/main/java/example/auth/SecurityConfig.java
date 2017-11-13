package example.auth;

import javax.servlet.Filter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(jsr250Enabled = true)
class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] PUBLIC_PATTERNS = {"/v2/**", "/swagger*/**"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        Filter filter = new AuthenticationFilter();
        http.authorizeRequests()
                .antMatchers(PUBLIC_PATTERNS).permitAll()
                .and()
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }

}
