package com.brokage.repository;

import com.brokage.modal.EmployeeEntity;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<EmployeeEntity, String> {
    Boolean existsByUserEntityUserName(String userName);
}
