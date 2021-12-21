package ir.imorate.cs50rm.command.security;

import ir.imorate.cs50rm.command.SecuredCommand;
import ir.imorate.cs50rm.shell.InputReader;
import ir.imorate.cs50rm.shell.ShellHelper;
import ir.imorate.cs50rm.utils.SecurityUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class AuthCommand extends SecuredCommand {

    private final ShellHelper shellHelper;
    private final InputReader inputReader;
    private final AuthenticationManager authenticationManager;

    public AuthCommand(@Lazy ShellHelper shellHelper, @Lazy InputReader inputReader,
                       AuthenticationManager authenticationManager) {
        this.shellHelper = shellHelper;
        this.inputReader = inputReader;
        this.authenticationManager = authenticationManager;
    }

    @ShellMethod("Login to Cs50RM")
    public void login() {
        if (SecurityUtils.isAuthenticated()) {
            shellHelper.printWarning("You are already logged in.");
            return;
        }
        Authentication request = new UsernamePasswordAuthenticationToken(
                inputReader.fieldPrompt("Username", null, true),
                inputReader.fieldPrompt("Password", null, false));
        try {
            Authentication result = authenticationManager.authenticate(request);
            SecurityContextHolder.getContext().setAuthentication(result);
            shellHelper.printSuccess("You have been logged in successfully.");
        } catch (AuthenticationException e) {
            shellHelper.printError("Login failed: " + e.getMessage());
        }
    }

    @ShellMethod("Logout from Cs50RM")
    public void logout() {
        if (!SecurityUtils.isAuthenticated()) {
            shellHelper.printWarning("You are not logged in.");
            return;
        }
        SecurityContextHolder.clearContext();
        shellHelper.printSuccess("You have been logged out successfully.");
    }

}
