package ir.imorate.cs50rm.config.shell;

import ir.imorate.cs50rm.utils.SecurityUtils;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

import java.util.Optional;

@Configuration
public class ShellPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        String str = "Cs50RM";
        if (SecurityUtils.isAuthenticated()) {
            Optional<String> authUser = SecurityUtils.getCurrentUserLogin();
            if (authUser.isPresent()) {
                return new AttributedString(String.format("%s - %s> ", str, authUser.get()),
                        AttributedStyle.BOLD.foreground(AttributedStyle.GREEN));
            }
        }
        return new AttributedString(String.format("%s> ", str), AttributedStyle.BOLD.foreground(AttributedStyle.BLUE));
    }

}
