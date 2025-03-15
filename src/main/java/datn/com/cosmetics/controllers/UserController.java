package datn.com.cosmetics.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import datn.com.cosmetics.bean.request.LoginRequest;
import datn.com.cosmetics.bean.request.RegisterRequest;
import datn.com.cosmetics.bean.request.UserRequest;
import datn.com.cosmetics.bean.response.ApiResponse;
import datn.com.cosmetics.bean.response.LoginResponse;
import datn.com.cosmetics.config.auth.JWTGenerator;
import datn.com.cosmetics.entity.User;
import datn.com.cosmetics.exceptions.UserNotFoundException;
import datn.com.cosmetics.services.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User API", description = "API for user operations")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private JWTGenerator jwtGenerator;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Operation(summary = "Register a new user", description = "Register a new user with email and password")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            if (user == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Email already exists"));
            }
            return ResponseEntity.ok(ApiResponse.success(user, "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request.getEmail(), request.getPassword());
            if (user == null) {
                throw new Exception("Invalid email or password");
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            LoginResponse loginResponse = new LoginResponse(user, jwtGenerator.generateToken(authentication));
            return ResponseEntity.ok(ApiResponse.success(loginResponse, "Login successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Get user profile", description = "Get the profile of the logged-in user")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> profile(
            @RequestHeader(name = "Authorization", required = false) String jwt) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
            }

            String username = authentication.getName();

            User user = userService.getUserByEmail(username);
            return ResponseEntity.ok(ApiResponse.success(user, "User info"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("User not found: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get user profile: " + e.getMessage()));
        }
    }

    @Operation(summary = "Update user information", description = "Update the avatar of the logged-in user")
    @PutMapping("/avatar")
    public ResponseEntity<ApiResponse<String>> updateUserInfo(
            @RequestHeader(name = "Authorization") String jwt,
            @RequestBody Map<String, String> avatar) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
            }

            String username = authentication.getName();
            boolean updated = userService.changeAvatar(username, avatar.get("avatar"));
            if (updated) {
                return ResponseEntity.ok(ApiResponse.success("User information updated successfully", ""));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update user information"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update avatar: " + e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<User>> putMethodName(@RequestHeader(name = "Authorization") String jwt,
            @RequestBody UserRequest entity) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
            }

            String username = authentication.getName();
            User updated = userService.updateUser(entity, username);
            if (updated != null) {
                return ResponseEntity.ok(ApiResponse.success(updated, "User information updated successfully"));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update user information"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update user: " + e.getMessage()));
        }
    }

    @Operation(summary = "Send OTP email", description = "Send an OTP to the user's email for verification")
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtpEmail(@RequestParam String email) {
        try {
            boolean sent = userService.sendOtpEmail(email);
            if (sent) {
                return ResponseEntity.ok(ApiResponse.success("", "OTP sent successfully"));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to send OTP"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("User not found: " + e.getMessage()));
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to send email: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to send OTP email: " + e.getMessage()));
        }
    }

    @Operation(summary = "Change user password", description = "Change the password of the user")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestHeader(name = "Authorization") String jwt,
            @RequestBody Map<String, String> body) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
            }

            String username = authentication.getName();
            boolean changed = userService.changePassword(username, body.get("oldPassword"), body.get("newPassword"));
            if (changed) {
                return ResponseEntity.ok(ApiResponse.success("Password changed successfully", ""));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to change password"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to change password: " + e.getMessage()));
        }
    }

    @Operation(summary = "Verify OTP", description = "Verify the OTP sent to the user's email")
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            boolean verified = userService.verifyOtp(email, otp);
            if (verified) {
                return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", ""));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to verify OTP"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to verify OTP: " + e.getMessage()));
        }
    }

    // get all user
    @Operation(summary = "Get all user", description = "Get all user")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<User>>> getAllUser(
            @RequestParam(required = false) String search, Pageable pageable

    ) {
        try {
            Page<User> users = userService.getAllUser(search, pageable);
            ApiResponse.Pagination pagination = new ApiResponse.Pagination(users.getNumber()+1, users.getTotalPages(),
                    users.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(users.getContent(), "Get all user successfully", pagination));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get all users: " + e.getMessage()));
        }
    }

    @PostMapping("/password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestParam String email,
            @RequestParam String password, @RequestParam String newPassword) {
        try {
            userService.changePassword(email, password, newPassword);
            return ResponseEntity.ok(ApiResponse.success("Change password successfully", ""));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to change password: " + e.getMessage()));
        }
    }

    // get user by id
    @Operation(summary = "Get user by id", description = "Get user by id")
    @GetMapping("/get")
    public ResponseEntity<ApiResponse<User>> getUserById(@RequestParam Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id), "Get user by id successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get user by id: " + e.getMessage()));
        }
    }

    // delete user by id
    @Operation(summary = "Delete user by id", description = "Delete user by id")
    @GetMapping("/delete")
    public ResponseEntity<ApiResponse<User>> deleteUserById(@RequestParam Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Delete user by id successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete user by id: " + e.getMessage()));
        }
    }

    // block user by id
    @Operation(summary = "Block user by id", description = "Block user by id")
    @PutMapping("/block/{id}")
    public ResponseEntity<ApiResponse<User>> blockUserById(@PathVariable Long id) {
        try {
            userService.blockUserById(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Block user by id successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to block user by id: " + e.getMessage()));
        }
    }

    // change role user by id
    @Operation(summary = "Change role user by id", description = "Change role user by id")
    @PutMapping("/role/{id}")
    public ResponseEntity<ApiResponse<User>> changeRoleUserById(@PathVariable Long id, @RequestParam String role) {
        try {
            userService.changeRoleUserById(id, role);
            return ResponseEntity.ok(ApiResponse.success(null, "Change role user by id successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to change role user by id: " + e.getMessage()));
        }
    }


}
