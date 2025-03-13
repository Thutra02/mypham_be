package datn.com.cosmetics.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import datn.com.cosmetics.bean.response.ApiResponse;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/public/hello")
    public ApiResponse<String> publicHello() {
        return ApiResponse.success("Hello Public!", "Public endpoint accessed successfully");
    }

    @GetMapping("/hello/test")
    public ApiResponse<String> helloTest() {
        return ApiResponse.success("Hello from protected endpoint!", "Access granted");
    }

    @GetMapping("/hello/user-check")
    public ApiResponse<String> checkUserRole(@RequestHeader(name = "Authorization", required = false) String jwt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponse.error("Not logged in");
        }

        String username = authentication.getName();
        System.out.println("Username: " + username);
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ApiResponse.success("You are an ADMIN", "User check successful");
        } else if (isUser) {
            return ApiResponse.success("You are a USER", "User check successful");
        } else {
            return ApiResponse.error("Unknown role");
        }
    }

    @GetMapping("/admin/hello")
    public String adminHello() {
        return "Hello Admin!";
    }
}
