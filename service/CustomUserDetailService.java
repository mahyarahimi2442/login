package ir.ac.loging.service;

import ir.ac.loging.model.User;
import ir.ac.loging.model.VerificationToken;
import ir.ac.loging.repository.UserRepository;
import ir.ac.loging.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
@Transactional
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository, VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;}
    private final VerificationTokenRepository tokenRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .build();
    }

        public User save(User user) {
            return userRepository.save(user);
        }

        public String generateVerificationCode() {
            Random random = new Random();
            int code = 100000 + random.nextInt(900000);
            return String.valueOf(code);
        }

        public void saveVerificationCode(String email, String code) {
            tokenRepository.deleteByEmail(email);
            VerificationToken token = new VerificationToken();
            token.setEmail(email);
            token.setCode(code);
            token.setExpiryDate(LocalDateTime.now().plusMinutes(2));
            tokenRepository.save(token);
        }

       public boolean verifyCode(String email, String code) {
        List<VerificationToken> tokens = tokenRepository.findByEmailAndCode(email, code);

        if (tokens.isEmpty()) return false;

        return tokens.stream()
                .anyMatch(t -> t.getExpiryDate().isAfter(LocalDateTime.now()));
    }



    public void deleteToken(String email) {
            tokenRepository.deleteByEmail(email);
        }

        public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("کاربر یافت نشد"));
    }

       public User findByUsername (String username) {
        return userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("کاربر یافت نشد"));
       }
    }




