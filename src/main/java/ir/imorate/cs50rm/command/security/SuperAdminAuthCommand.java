package ir.imorate.cs50rm.command.security;

import ir.imorate.cs50rm.command.SecuredCommand;
import ir.imorate.cs50rm.entity.security.Role;
import ir.imorate.cs50rm.entity.security.User;
import ir.imorate.cs50rm.entity.security.enums.RoleType;
import ir.imorate.cs50rm.exception.security.UserNotFoundException;
import ir.imorate.cs50rm.service.security.RoleService;
import ir.imorate.cs50rm.service.security.UserService;
import ir.imorate.cs50rm.shell.InputReader;
import ir.imorate.cs50rm.shell.ShellHelper;
import ir.imorate.cs50rm.utils.TableUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import java.util.*;

@ShellComponent
public class SuperAdminAuthCommand extends SecuredCommand {

    private final ShellHelper shellHelper;
    private final InputReader inputReader;
    private final UserService userService;
    private final RoleService roleService;

    public SuperAdminAuthCommand(@Lazy ShellHelper shellHelper, @Lazy InputReader inputReader, UserService userService,
                                 RoleService roleService) {
        this.shellHelper = shellHelper;
        this.inputReader = inputReader;
        this.userService = userService;
        this.roleService = roleService;
    }

    @ShellMethodAvailability({"isUserSuperAdmin"})
    @ShellMethod(value = "Add a new admin", key = "auth add-admin")
    public void addAdmin() {
        String username = inputReader.fieldPrompt("Username", null, true);
        if (userService.userExists(username)) {
            shellHelper.printError(String.format("Username %s is exists on database", username));
            return;
        }
        String email = inputReader.fieldPrompt("Email", null, true);
        if (userService.userEmailExists(email)) {
            shellHelper.printError(String.format("Email %s is exists on database", username));
            return;
        }
        String password = inputReader.fieldPrompt("Password", null, false);
        RoleType roleType = getRoleType();
        User user = User.builder().username(username).email(email).password(password).enabled(true)
                .accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).build();
        userService.createAdmin(user, roleType);
        shellHelper.printInfo(TableUtils.beanTable(user, Map.of("email", "Email", "username", "Username"), 80));
        shellHelper.printSuccess(String.format("Admin with username \"%s\" created successfully.", username));
    }

    @ShellMethodAvailability({"isUserSuperAdmin"})
    @ShellMethod(value = "Change admin role", key = "auth change-role")
    public void changeRole(@ShellOption({"-u", "--username"}) String username) {
        try {
            User user = userService.findUser(username);
            if (isRoleExist(RoleType.ROLE_SUPER_ADMIN, user.getRoles())) {
                shellHelper.printError(String.format("You are %s already, You can't change your own role.",
                        RoleType.ROLE_SUPER_ADMIN.getTitle()));
                return;
            }
            RoleType roleType = getRoleType();
            Role role = roleService.findRole(roleType);
            user.setRoles(Set.of(role));
            userService.save(user);
            shellHelper.printSuccess(String.format("Username \"%s\" role was successfully changed to %s.",
                    username, roleType.getTitle()));
        } catch (UserNotFoundException e) {
            shellHelper.printError(String.format("Username \"%s\" not found.", username));
        }
    }

    @ShellMethodAvailability({"isUserSuperAdmin"})
    @ShellMethod(value = "Show all admins", key = "auth list")
    public void listAdmins() {
        List<User> users = userService.findAll();
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("username", "Username");
        headers.put("email", "Email");
        headers.put("enabled", "Enabled");
        headers.put("roles", "Role(s)");
        TableModel model = new BeanListTableModel<>(users, headers);
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.fancy_light);
        shellHelper.printInfo(tableBuilder.build().render(80));
        shellHelper.printSuccess(String.format("Found %s record(s) on database.", users.size()));
    }

    @ShellMethodAvailability({"isUserSuperAdmin"})
    @ShellMethod(value = "Remove an admin account", key = "auth remove-admin")
    public void removeAdmin(@ShellOption({"-u", "--username"}) String username) {
        try {
            User user = userService.findUser(username);
            if (isRoleExist(RoleType.ROLE_SUPER_ADMIN, user.getRoles())) {
                shellHelper.printError("You cant remove a super admin account.");
                return;
            }
            userService.remove(user);
            shellHelper.printSuccess(String.format("Admin account with username \"%s\" was removed successfully.", username));
        } catch (UserNotFoundException e) {
            shellHelper.printError(String.format("Username \"%s\" not found.", username));
        }
    }

    @ShellMethodAvailability({"isUserSuperAdmin"})
    @ShellMethod(value = "Edit an admin account", key = "auth edit-admin")
    public void editAdmin(@ShellOption({"-u", "--username"}) String username) {
        try {
            User user = userService.findUser(username);
            if (isRoleExist(RoleType.ROLE_SUPER_ADMIN, user.getRoles())) {
                shellHelper.printError("You cant edit a super admin account.");
                return;
            }
            shellHelper.printInfo("Leave blank if you want to use previous value.");
            String newUsername = inputReader.fieldPrompt("Username", user.getUsername(), true);
            if (userService.userExists(newUsername) && !newUsername.equals(username)) {
                shellHelper.printError(String.format("Username %s is exists on database", newUsername));
                return;
            }
            String email = inputReader.fieldPrompt("Email", user.getEmail(), true);
            if (userService.userEmailExists(email) && !user.getEmail().equals(email)) {
                shellHelper.printError(String.format("Email %s is exists on database", username));
                return;
            }
            String password = inputReader.fieldPrompt("Password", user.getPassword(), false);
            user.setUsername(newUsername);
            user.setEmail(email);
            user.setPassword(password);
            userService.edit(user);
            shellHelper.printInfo(TableUtils.beanTable(user, Map.of("username", "Username", "email", "Email"), 80));
            shellHelper.printSuccess(String.format("Admin with username \"%s\" updated successfully.", username));
        } catch (UserNotFoundException e) {
            shellHelper.printError(String.format("Username \"%s\" not found.", username));
        }
    }

    private Map<String, String> roleOptionList() {
        Map<String, String> roles = new HashMap<>();
        roles.put("A", RoleType.ROLE_ADMIN.getTitle());
        roles.put("S", RoleType.ROLE_SUPER_ADMIN.getTitle());
        return roles;
    }

    private RoleType getRoleType() {
        Map<String, String> roles = roleOptionList();
        String roleValue = inputReader.selectFromList("Role",
                "Please enter one of the roles [Admin]", roles, true, "A");
        return Arrays.stream(RoleType.values()).filter(type -> type.getTitle().equals(roles.get(roleValue)))
                .findFirst().orElseThrow(() -> new RuntimeException("Role type not found"));
    }

    private boolean isRoleExist(RoleType roleType, Set<Role> roles) {
        for (Role role : roles) {
            if (role.getType().equals(roleType)) {
                return true;
            }
        }
        return false;
    }

}
