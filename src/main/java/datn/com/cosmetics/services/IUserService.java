package datn.com.cosmetics.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import datn.com.cosmetics.bean.request.RegisterRequest;
import datn.com.cosmetics.bean.request.UserRequest;
import datn.com.cosmetics.entity.User;
import datn.com.cosmetics.exceptions.UserNotFoundException;
import jakarta.mail.MessagingException;

public interface IUserService {
    User login(String username, String password);

    User register(RegisterRequest user);

    User getUserInfo(String email);

    boolean changeAvatar(String email, String avatarUrl);

    User getUserById(Long id);

    boolean changePassword(String email,  String oldPassword,String newPassword );

    boolean sendOtpEmail(String email) throws MessagingException, UserNotFoundException;

    boolean verifyOtp(String email, String otp);

    User updateUser(UserRequest userRequest, String email);

    User getUserByEmail(String email) throws UserNotFoundException;

    List<User> getAllUser();
    Page<User> getAllUser(String name, Pageable pageable);

    boolean deleteUserById(Long id);

    boolean blockUserById(Long id);

    void changeRoleUserById(Long id,String role);
}
