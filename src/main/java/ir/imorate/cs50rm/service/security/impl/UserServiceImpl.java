package ir.imorate.cs50rm.service.security.impl;

import ir.imorate.cs50rm.entity.security.Role;
import ir.imorate.cs50rm.entity.security.User;
import ir.imorate.cs50rm.entity.security.enums.RoleType;
import ir.imorate.cs50rm.exception.security.UserNotFoundException;
import ir.imorate.cs50rm.repository.security.UserRepository;
import ir.imorate.cs50rm.service.security.RoleService;
import ir.imorate.cs50rm.service.security.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleService roleService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username).map(this::createSpringSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private UserDetails createSpringSecurityUser(User user) {
        List<GrantedAuthority> grantedAuthorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getType().name())).collect(Collectors.toList());
        return org.springframework.security.core.userdetails.User.builder().username(user.getUsername())
                .password(user.getPassword()).disabled(false).accountExpired(false).credentialsExpired(false)
                .accountLocked(false).authorities(grantedAuthorities).build();
    }

    @Override
    public User findUser(String username) throws UserNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with username %s not found.", username)));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void remove(User user) {
        userRepository.delete(user);
    }

    @Override
    public void edit(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findByUsernameIgnoreCase(username).isPresent();
    }

    @Override
    public boolean userEmailExists(String email) {
        return userRepository.findByEmailIgnoreCase(email).isPresent();
    }

    @Override
    public void createAdmin(@Valid User user, RoleType roleType) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(roleService.findRole(roleType));
        user.setRoles(roleSet);
        userRepository.save(user);
    }
}
