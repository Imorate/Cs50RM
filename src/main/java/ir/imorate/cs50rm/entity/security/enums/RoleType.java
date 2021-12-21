package ir.imorate.cs50rm.entity.security.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType {

    ROLE_ADMIN("Admin"),
    ROLE_SUPER_ADMIN("Super Admin");

    private final String title;

}
