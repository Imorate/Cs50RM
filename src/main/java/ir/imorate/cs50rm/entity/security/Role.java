package ir.imorate.cs50rm.entity.security;

import ir.imorate.cs50rm.entity.security.enums.RoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Table(name = "role")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @ToString.Exclude
    private Integer id;

    @NotNull(message = "Role may not be blank")
    @Enumerated(EnumType.STRING)
    @Column(name = "title", nullable = false, unique = true)
    private RoleType type;

    @NotBlank(message = "Role may not be blank")
    @Size(message = "Role description must have value up to 16", max = 16)
    @Column(name = "description", nullable = false, length = 16)
    private String description;

    @Override
    public String toString() {
        return type.getTitle();
    }
}