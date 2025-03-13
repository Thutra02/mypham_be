package datn.com.cosmetics.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

import datn.com.cosmetics.services.EmailService;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/send-welcome-email")
    public String sendWelcomeEmail(@RequestParam String to) throws Exception {
        Context context = new Context();
        context.setVariable("name", "Người Dùng"); // Truyền dữ liệu động

        emailService.sendEmail(to, "Chào mừng bạn!", "welcome-email", context);
        return "Email chào mừng đã được gửi!";
    }

    @GetMapping("/send-reset-password-email")
    public String sendResetPasswordEmail(@RequestParam String to) throws Exception {
        Context context = new Context();
        context.setVariable("name", "Người Dùng");
        context.setVariable("token", "ABC123XYZ"); // Truyền dữ liệu động

        emailService.sendEmail(to, "Đặt lại mật khẩu", "reset-password", context);
        return "Email đặt lại mật khẩu đã được gửi!";
    }
}