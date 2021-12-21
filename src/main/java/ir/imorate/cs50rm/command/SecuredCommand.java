package ir.imorate.cs50rm.command;

import ir.imorate.cs50rm.entity.security.enums.RoleType;
import ir.imorate.cs50rm.utils.SecurityUtils;
import org.springframework.shell.Availability;

public abstract class SecuredCommand {

    public Availability isUserLoggedIn() {
        if (!SecurityUtils.isAuthenticated()) {
            return Availability.unavailable("You are not logged in!");
        }
        return Availability.available();
    }

    public Availability isUserSuperAdmin() {
        if (!SecurityUtils.isAuthenticated()) {
            return Availability.unavailable("You are not logged in!");
        }
        if (SecurityUtils.hasCurrentUserThisAuthority(RoleType.ROLE_SUPER_ADMIN.name())) {
            return Availability.available();
        }
        return Availability.unavailable("You don't have the right access!");
    }

}
