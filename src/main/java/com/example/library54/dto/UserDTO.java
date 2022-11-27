package com.example.library54.dto;

import com.example.library54.domain.Loan;
import com.example.library54.domain.Role;
import com.example.library54.domain.User;
import com.example.library54.domain.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String address;

    private String phone;

    private Date birthDate;

    private String email;

    private LocalDateTime createDate ;

    private Boolean builtIn ;

    private Set<String> roles;

    public void setRoles(Set<Role> roles) {
        Set<String> rolesStr = new HashSet<>();

        roles.forEach(m-> {
            if (m.getName().equals(RoleType.ROLE_ADMIN))
                rolesStr.add("Administrator");
            else if(m.getName().equals(RoleType.ROLE_MEMBER))
                rolesStr.add("Member");
            else if(m.getName().equals(RoleType.ROLE_EMPLOYEE))
                rolesStr.add("Employee");
            else
                rolesStr.add("Anonymous");
        });

        this.roles=rolesStr;
    }

    private List<Loan> loan;

    public UserDTO(User user){
        this.id=user.getId();
        this.firstName=user.getFirstName();
        this.lastName= user.getLastName();
        this.address= user.getAddress();
        this.phone= user.getPhone();
        this.birthDate= user.getBirthDate();
        this.email=user.getEmail();
        this.builtIn=user.getBuiltIn();
        this.roles= Collections.singleton(user.getRoles().toString());
        this.loan=user.getLoan();
    }

}
