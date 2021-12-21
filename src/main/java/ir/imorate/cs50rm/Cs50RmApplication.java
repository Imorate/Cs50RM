package ir.imorate.cs50rm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@EnableJpaAuditing
public class Cs50RmApplication {

    public static void main(String[] args) {
        SpringApplication.run(Cs50RmApplication.class, args);
    }

}
