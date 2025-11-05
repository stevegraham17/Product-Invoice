package com.products.products.config;

import com.products.products.models.Invoice;
import com.products.products.models.User;
import com.products.products.repositories.InvoiceRepository;
import com.products.products.repositories.UserRepository;
import com.products.products.services.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSessionEvent;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
	
@Autowired
private InvoiceRepository invoiceRepository;
	
@Autowired
private UserRepository userRepository;


@Autowired
private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // Constructor-based dependency injection for UserService
    public SecurityConfig(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);  // Use the custom UserService
        authProvider.setPasswordEncoder(passwordEncoder); // Get PasswordEncoder directly
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .headers(headers -> headers
                    .frameOptions(frame -> frame.sameOrigin()))
            .authorizeHttpRequests((authorize) ->
                authorize
                    .requestMatchers("/products/**").hasRole("ADMIN")
                    .requestMatchers("/billing/**").hasRole("CASHIER")
                    .requestMatchers("/invoices/**").hasRole("SIGNER")  
                    .requestMatchers("/register", "/login", "/forgot-password","/reset-password","/check-username","/check-email", "/css/**", "/js/**","/logo.png").permitAll() // Allow access to forgot-password
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")  // Custom login page
                .permitAll()
                .successHandler(authenticationSuccessHandler()) // Use custom success handler
                .failureUrl("/login?error=true")  // Redirect to login page on failure with error=true
            )
            .logout(logout -> logout
                    .permitAll()
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true) // Invalidate session on logout
                    .clearAuthentication(true) // Clear authentication
            )
        .exceptionHandling(ex -> 
        ex.accessDeniedHandler((request, response, exception) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                response.sendRedirect("/products");
            } else if (auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_CASHIER"))) {
                response.sendRedirect("/billing"); // or /cashier depending on your setup
            } else {
                response.sendRedirect("/login?error=unauthorized");
            }
        })
    );

        return http.build();
    }


 /*   @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            // Check user roles and redirect accordingly
            String redirectUrl = "/products"; // Default redirect URL

            if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                redirectUrl = "/products"; // Admin redirect
            } else if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_CASHIER"))) {
                redirectUrl = "/billing"; // Cashier redirect
            }else if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_SIGNER"))) {

                String email = authentication.getName(); // signer’s username/email
                List<Invoice> invoices = invoiceRepository.findAllBySignerEmail(email);
                if (!invoices.isEmpty()) {
                    redirectUrl = "/invoices/" + invoices.get(0).getId() + "/sign";
                } else {
                    redirectUrl = "/signing"; // fallback
                }

            }

            response.sendRedirect(redirectUrl); // Redirect based on role
        };
    }*/
    
 /*   @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {

            String email = authentication.getName(); // username/email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // 1️⃣ Check for force password reset first
            if (user.isForcePasswordReset()) {
                response.sendRedirect("/signer/reset-password");
                return; // important to stop further processing
            }

            // 2️⃣ Normal role-based redirect
            String redirectUrl = "/products"; // default

            if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                redirectUrl = "/products";
            } else if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_CASHIER"))) {
                redirectUrl = "/billing";
            } else if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_SIGNER"))) {

                List<Invoice> invoices = invoiceRepository.findAllBySignerEmail(email);
                if (!invoices.isEmpty()) {
                    redirectUrl = "/invoices/" + invoices.get(0).getId() + "/sign";
                } else {
                    redirectUrl = "/signing";
                }
            }

            response.sendRedirect(redirectUrl);
        };
    }*/
    
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {

            String email = authentication.getName(); // username/email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // 1️⃣ Check for force password reset first
            if (user.isForcePasswordReset()) {
                response.sendRedirect("/signer/reset-password");
                return; // important to stop further processing
            }

            // 2️⃣ Normal role-based redirect
            String redirectUrl = "/products"; // default

            if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                redirectUrl = "/products";
            } else if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_CASHIER"))) {
                redirectUrl = "/billing";
            } else if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_SIGNER"))) {

                // ✅ Signer logic: redirect to the URL the user originally requested (from email)
                SavedRequest savedRequest = (SavedRequest) request.getSession()
                        .getAttribute("SPRING_SECURITY_SAVED_REQUEST");

                if (savedRequest != null) {
                    // Use the original requested URL
                    redirectUrl = savedRequest.getRedirectUrl();
                } else {
                    // Fallback if no saved request: pick the first invoice
                    List<Invoice> invoices = invoiceRepository.findAllBySignerEmail(email);
                    if (!invoices.isEmpty()) {
                        redirectUrl = "/invoices/" + invoices.get(0).getId() + "/sign";
                    } else {
                        redirectUrl = "/signing";
                    }
                }
            }

            response.sendRedirect(redirectUrl);
        };
    }


    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher() {
            @Override
            public void sessionDestroyed(HttpSessionEvent event) {
                SecurityContextHolder.clearContext(); // Ensure the security context is cleared on session destroy
                super.sessionDestroyed(event);
            }
        };
    }
    
   


}
