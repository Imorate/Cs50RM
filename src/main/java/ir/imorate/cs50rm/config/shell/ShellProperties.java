package ir.imorate.cs50rm.config.shell;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shell.out")
@Getter
@Setter
public class ShellProperties {

    /**
     * Info shell output
     */
    private String info;

    /**
     * Success shell output
     */
    private String success;

    /**
     * Warning shell output
     */
    private String warning;

    /**
     * Error shell output
     */
    private String error;

}
