package ir.ac.loging.controler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class PageController {


    @GetMapping("/register")
    public String register() {return "register";}

    @GetMapping("/verify")
    public String verify() {return "verify";}


    @GetMapping("/login")
    public String login() {return "login";}




}
