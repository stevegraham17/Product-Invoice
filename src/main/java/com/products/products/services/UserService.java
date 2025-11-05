package com.products.products.services;

import com.products.products.models.AccountType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.products.products.models.Company;
import com.products.products.models.PasswordResetToken;
import com.products.products.repositories.CompanyRepository;
import com.products.products.repositories.PasswordResetTokenRepository;
import com.products.products.models.User;
import com.products.products.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import com.products.products.models.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    
    public UserService(UserRepository userRepository, CompanyRepository companyRepository,PasswordEncoder passwordEncoder) {
    	this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }
    
    public Company getCurrentLoggedInUserCompany() {
    	try {
            String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            logger.info("Retrieving company for username: {}", username);
            
            User user = userRepository.findByUsername(username);
            if (user == null) {
                logger.error("No user found with username: {}", username);
                return null;
            }

            if (user.getCompany() == null) {
                logger.error("User found, but company is null for username: {}", username);
                return null;
            }
            
            logger.info("Retrieved company_id: {}", user.getCompany().getId());
            return user.getCompany();
        } catch (Exception e) {
            logger.error("Error while retrieving company for the logged-in user: {}", e.getMessage());
            return null;
        } // Now you can safely return the company
    }
    
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
 // Method to find user by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // Method to update user details
    public void updateUser(User user) {
        userRepository.save(user);
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username); // Directly fetch User
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getAccountType().name())
                .build();
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null; // Check if User is not null
    }

    

    public boolean isEmailRegistered(String email) {
    	return userRepository.findByEmail(email).isPresent();
    }
    @Transactional
    public void registerUser(UserRegistrationDto registrationDto) {
        // Check if the username already exists
        if (usernameExists(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        // Check if the email is already registered
        if (isEmailRegistered(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // Create a new User object from the registration DTO
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword())); // Encode the password

        // Set the account type
        if (registrationDto.getAccountType() != null) {
            user.setAccountType(User.AccountType.valueOf(registrationDto.getAccountType().toUpperCase()));
        } else {
            throw new IllegalArgumentException("Account type is required");
        }

        // Set company based on account type
        if ("CASHIER".equals(registrationDto.getAccountType())) {
            user.setCompany(null); // Set company to null for CASHIER
        } else {
            // For Admin or other account types, create or find the company
            Company company = new Company();
            company.setName(registrationDto.getCompanyName());  // Assuming Account Name is the Company Name
            companyRepository.save(company);
            user.setCompany(company); // Set the company for Admins
        }

        // Save the user to the database
        userRepository.save(user);
    }

    
    public Company getCompanyById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + companyId));
    }




    public String getAccountType(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user.getAccountType().name();
    }
    
    
    @Transactional
    public boolean assignCompanyToCashier(String cashierEmail, String adminUsername) {
        // Fetch the Admin user by username
        System.out.println("Checking admin for username: " + adminUsername);
        User admin = userRepository.findByUsername(adminUsername); // Fetch by username

        // Check if admin exists and is of type ADMIN
        if (admin == null || !admin.getAccountType().equals(User.AccountType.ADMIN)) {
            System.out.println("Admin user not found or is not an ADMIN.");
            return false; // Return false if Admin doesn't exist or is not ADMIN
        }

        Company adminCompany = admin.getCompany(); // Fetch the Admin's company
        if (adminCompany == null) {
            System.out.println("Admin does not have an associated company.");
            return false; // Return false if Admin doesn't have a company
        }

        // Find the Cashier by email
        Optional<User> cashierOptional = userRepository.findByEmail(cashierEmail);
        if (cashierOptional.isEmpty()) {
            System.out.println("Cashier user not found with email: " + cashierEmail);
            return false; // This indicates that the cashier doesn't exist
        }

        User cashier = cashierOptional.get(); // Get the Cashier user

        // Check if the cashier account type is CASHIER
        if (!cashier.getAccountType().equals(User.AccountType.CASHIER)) {
            System.out.println("User found, but they are not a CASHIER.");
            return false; // Return false if the user is not a CASHIER
        }

        // Assign the Admin's Company to the Cashier
        cashier.setCompany(adminCompany); // Assign Admin's company to Cashier
        System.out.println("Admin Username: " + adminUsername);
        System.out.println("Cashier Email: " + cashierEmail);

        userRepository.save(cashier); // Save the updated Cashier

        return true; // Return true if successfully assigned
    }







    





    // Other methods remain unchanged...

    // Method to generate and save password reset token
    public void createPasswordResetToken(User user) {
        // Check if an existing token for the user exists and delete it
        passwordResetTokenRepository.findByUserId(user.getId()).ifPresent(existingToken -> {
            passwordResetTokenRepository.delete(existingToken);
        });

        // Generate a new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        passwordResetTokenRepository.save(resetToken);
    }

    // Method to send password reset email
    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        createPasswordResetToken(user); // Create the token

        // Fetch the latest token for the user
        PasswordResetToken latestToken = passwordResetTokenRepository.findFirstByUserIdOrderByExpiryDateDesc(user.getId())
                .orElseThrow(() -> new IllegalStateException("Failed to create password reset token."));

        // Build the reset URL using the latest token
        String resetUrl = "http://localhost:8080/reset-password?token=" + latestToken.getToken();

        // Prepare and send the email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n" + resetUrl);
        mailSender.send(message);
    }

    // Method to reset password using token
 // Method to reset password using token
   
    public void resetPassword(String token, String newPassword) {
        // Fetch the reset token using the provided token string
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        // Check if the token has expired
        if (LocalDateTime.now().isAfter(resetToken.getExpiryDate())) {
            throw new IllegalArgumentException("Token has expired");
        }

        // Fetch the user from the reset token
        User user = resetToken.getUser(); // Directly accessing the user from the token
        user.setPassword(passwordEncoder.encode(newPassword)); // Encode the new password
        userRepository.save(user); // Save the updated user.
        // Optionally, delete the token after it has been used
        passwordResetTokenRepository.delete(resetToken);
    }
    
    public User createOrGetSigner(String email) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User signer = new User();
            signer.setUsername(email);
            signer.setEmail(email);

            String tempPassword = "Temp123!"; // could be randomly generated
            signer.setPassword(passwordEncoder.encode(tempPassword));

            signer.setAccountType(User.AccountType.SIGNER);
            signer.setForcePasswordReset(true);  // force first login password reset
            return userRepository.save(signer);
        });
    }
    
    @Transactional
    public void resetPasswordByEmail(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setForcePasswordReset(false); // important: remove force reset flag
        userRepository.save(user);
    }



}
