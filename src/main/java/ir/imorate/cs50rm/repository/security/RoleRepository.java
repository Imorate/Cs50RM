package ir.imorate.cs50rm.repository.security;

import ir.imorate.cs50rm.entity.security.Role;
import ir.imorate.cs50rm.entity.security.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByType(RoleType type);

}