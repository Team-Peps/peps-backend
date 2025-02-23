package fr.teampeps.security.config;

import fr.teampeps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    /**
     * Custom UserDetailsService to load user details.
     *
     * @return UserDetailsService implementation based on the provided username or email.
     */
    @Bean
    public UserDetailsService userDetailsService(){
        return usernameOrEmail -> {
            if(usernameOrEmail.contains("@")){
                return userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }else{
                return userRepository.findByUsername(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    /**
     * Custom AuthenticationProvider for database-based authentication.
     *
     * @return AuthenticationProvider configured with custom UserDetailsService and password encoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Custom AuthenticationManager configuration.
     *
     * @param config AuthenticationConfiguration for retrieving the authentication manager.
     * @return AuthenticationManager for handling authentication requests.
     * @throws Exception If there is an error configuring the authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    /**
     * Password encoder for securely hashing passwords.
     *
     * @return PasswordEncoder implementation, using BCrypt hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
