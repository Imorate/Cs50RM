package ir.imorate.cs50rm.service.security;

import ir.imorate.cs50rm.entity.security.User;
import ir.imorate.cs50rm.entity.security.enums.RoleType;

import java.util.List;

public interface UserService {

    User findUser(String username);

    List<User> findAll();

    void save(User user);

    void remove(User user);

    void edit(User user);

    boolean userExists(String username);

    boolean userEmailExists(String email);

    void createAdmin(User user, RoleType roleType);

}
