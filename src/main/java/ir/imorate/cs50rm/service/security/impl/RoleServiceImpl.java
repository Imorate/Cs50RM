package ir.imorate.cs50rm.service.security.impl;

import ir.imorate.cs50rm.entity.security.Role;
import ir.imorate.cs50rm.entity.security.enums.RoleType;
import ir.imorate.cs50rm.exception.security.RoleNotFoundException;
import ir.imorate.cs50rm.repository.security.RoleRepository;
import ir.imorate.cs50rm.service.security.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role findRole(RoleType roleType) throws RoleNotFoundException {
        return roleRepository.findByType(roleType)
                .orElseThrow(() -> new RoleNotFoundException(String.format("Role %s not found", roleType.getTitle())));
    }

}
