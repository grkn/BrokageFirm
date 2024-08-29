package com.brokage.modal;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserEntity extends BaseEntity {

    private String userName;
    private String password;

    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<AuthorizationEntity> authorizations;

    @OneToOne
    private EmployeeEntity employeeEntity;
    @OneToOne
    private CustomerEntity customerEntity;

}