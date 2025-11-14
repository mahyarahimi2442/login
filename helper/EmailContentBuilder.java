package ir.ac.loging.helper;

import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
    public class EmailContentBuilder {

        public String buildVerificationEmail(String username, String code) throws IOException {

            String html = StreamUtils.copyToString(
                    getClass().getResourceAsStream("/templates/sendEmail.html"),
                    StandardCharsets.UTF_8
            );


            html = html.replace("{{username}}", username);
            html = html.replace("{{code}}", code);

            return html;
        }
    }

