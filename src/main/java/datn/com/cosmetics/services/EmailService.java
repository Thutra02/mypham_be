package datn.com.cosmetics.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String templateName, Context context) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        // Thiết lập thông tin email
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("trathu600@gmail.com"); // Thay bằng email của bạn

        // Render template Thymeleaf thành HTML
        String htmlContent = templateEngine.process(templateName, context);
        helper.setText(htmlContent, true); // true để gửi dưới dạng HTML

        // Gửi email
        mailSender.send(mimeMessage);
    }

    public void sendOrderEmail(String to, String subject, String templateName, Map<String, Object> model)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Tạo nội dung email từ template Thymeleaf
        Context context = new Context();
        context.setVariables(model);
        String htmlContent = templateEngine.process(templateName, context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom("trathu600@gmail.com");

        mailSender.send(message);
    }

}