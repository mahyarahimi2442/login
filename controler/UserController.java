package ir.ac.loging.controler;

import ir.ac.loging.dto.UserDto;
import ir.ac.loging.helper.EmailContentBuilder;
import ir.ac.loging.model.User;
import ir.ac.loging.service.CustomUserDetailService;
import ir.ac.loging.service.EmailSender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/users")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailService customUserDetailService;
    private final AuthenticationManager authenticationManager;
    private final EmailSender emailSender;
    private final EmailContentBuilder emailContentBuilder;

    public UserController(
            PasswordEncoder passwordEncoder,
            CustomUserDetailService customUserDetailService,
            AuthenticationManager authenticationManager,
            EmailSender emailSender,
            EmailContentBuilder emailContentBuilder
    ) {
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailService = customUserDetailService;
        this.authenticationManager = authenticationManager;
        this.emailSender = emailSender;
        this.emailContentBuilder = emailContentBuilder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) {

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        user.setVerified(false);
        customUserDetailService.save(user);


        String code = customUserDetailService.generateVerificationCode();


        customUserDetailService.saveVerificationCode(user.getEmail(), code);

        try {

            String html = emailContentBuilder.buildVerificationEmail(user.getUsername(), code);


            emailSender.sendHtmlEmail(user.getEmail(), "کد تأیید حساب", html);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("خطا در ارسال ایمیل: " + e.getMessage());
        }

        return ResponseEntity.ok("ثبت‌نام انجام شد. لطفا ایمیل خود را بررسی کنید.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean valid = customUserDetailService.verifyCode(email, code);
        if (!valid) {
            return ResponseEntity.badRequest().body("کد اشتباه یا منقضی شده است");
        }


        User user = customUserDetailService.findByEmail(email);
        user.setVerified(true);
        customUserDetailService.save(user);


        customUserDetailService.deleteToken(email);

        return ResponseEntity.ok("حساب کاربری با موفقیت فعال شد ✅");
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpServletRequest request) {

        User user = customUserDetailService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(401).body("نام کاربری یا رمز اشتباه است");
        }

        if (!user.isVerified()) {
            return ResponseEntity.status(403).body("حساب کاربری شما هنوز فعال نشده است. لطفا ایمیل خود را تایید کنید.");
        }


        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(username, password);
        try {
            Authentication authenticate = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authenticate);

            HttpSession session = request.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(401).body("نام کاربری یا رمز اشتباه است");
        }
    }

}