package ir.ac.loging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ir.ac.loging")
public class LogingApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogingApplication.class, args);
        System.out.println("start app");
    }
    }


