package datn.com.cosmetics.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import datn.com.cosmetics.entity.User;
import datn.com.cosmetics.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email); // Tìm theo email
    if (user == null) {
      throw new UsernameNotFoundException("User not found with email: " + email);
    }
    return org.springframework.security.core.userdetails.User
        .withUsername(user.getEmail()) // Sử dụng email làm username trong UserDetails
        .password(user.getPassword())
        .roles(user.getRole())
        .build();
  }
}