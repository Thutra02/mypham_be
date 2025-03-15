package datn.com.cosmetics.services.impl;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import datn.com.cosmetics.bean.request.RegisterRequest;
import datn.com.cosmetics.bean.request.UserRequest;
import datn.com.cosmetics.entity.Cart;
import datn.com.cosmetics.entity.User;
import datn.com.cosmetics.exceptions.UserNotFoundException;
import datn.com.cosmetics.repository.CartRepository;
import datn.com.cosmetics.repository.UserRepository;
import datn.com.cosmetics.services.EmailService;
import datn.com.cosmetics.services.IUserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User login(String username, String password) {
        try {
            User user = userRepository.findByEmail(username);
            if (user != null && user.isLocked() == true) {
                throw new Exception("User is locked");
            }
            if (user != null && passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public User register(RegisterRequest user) {
        try {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            User newUser = new User();
            newUser.setUsername(user.getUsername());
            newUser.setPassword(user.getPassword());
            newUser.setEmail(user.getEmail());

            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(newUser);
            Cart cart = new Cart();
            cart.setUser(savedUser);
            cartRepository.save(cart);
            return savedUser;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public User getUserInfo(String email) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new UserNotFoundException("User not found with email: " + email);
            }
            return user;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public boolean changeAvatar(String email, String avatarUrl) {
        try {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                user.setAvatar(avatarUrl);
                userRepository.save(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public User getUserById(Long id) {
        try {
            return userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user by id: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean sendOtpEmail(String email) throws MessagingException, UserNotFoundException {
        try {
            User userOpt = userRepository.findByEmail(email);
            if (userOpt == null) {
                throw new UserNotFoundException("User not found with email: " + email);
            }
            // Generate OTP and send email using EmailService
            String otp = generateOtp();
            Context context = new Context();
            context.setVariable("otp", otp);
            context.setVariable("name", userOpt.getUsername()); // Assuming username is the name

            emailService.sendEmail(email, "OTP Verification", "otp-email-template", context);
            User user = userOpt;
            user.setOtp(otp);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        try {
            User userOpt = userRepository.findByEmail(email);
            if (userOpt == null) {
                return false;
            }
            if (userOpt != null && userOpt.getOtp().equals(otp)) {
                userOpt.setOtp(null);
                userRepository.save(userOpt);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        try {
            User userOpt = userRepository.findByEmail(email);
            if (userOpt != null) {
                userOpt.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(userOpt);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Failed to change password: " + e.getMessage(), e);
        }
    }

    @Override
    public User getUserByEmail(String email) throws UserNotFoundException {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new UserNotFoundException("User not found with email: " + email);
            }
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user by email: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getAllUser() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all users: " + e.getMessage(), e);
        }
    }

    @Override
    public void changeRoleUserById(Long id, String role) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user != null) {
                user.setRole(role);
                userRepository.save(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to change user role by id: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteUserById(Long id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user by id: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean blockUserById(Long id) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user != null) {
                user.setLocked(!user.isLocked());
                userRepository.save(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Failed to toggle user lock status by id: " + e.getMessage(), e);
        }
    }

    @Override
    public User updateUser(UserRequest user, String email) {
        try {
            User userOpt = userRepository.findByEmail(email);
            if (userOpt != null) {
                userOpt.setUsername(user.getUsername());
                userOpt.setBio(user.getBio());
                userOpt.setPhone(user.getPhone());
                User newUser = userRepository.save(userOpt);
                return newUser;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<User> getAllUser(String name, org.springframework.data.domain.Pageable pageable) {
        try {
            if (name != null) {
                return userRepository.findByEmailContaining(name, pageable);
            }
            return userRepository.findAll(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all users with pagination: " + e.getMessage(), e);
        }
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

}
