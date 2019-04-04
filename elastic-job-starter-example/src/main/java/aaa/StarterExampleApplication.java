package aaa;

import com.helper.starter.annotation.EnableEasyJobConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEasyJobConfiguration
public class StarterExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(StarterExampleApplication.class, args);
        System.out.println("aaa");
    }
}
