package ir.imorate.cs50rm.service.security;

import ir.imorate.cs50rm.entity.security.Role;
import ir.imorate.cs50rm.entity.security.enums.RoleType;

public interface RoleService {

    Role findRole(RoleType roleType);

}
