package com.brokage.repository;

import com.brokage.modal.CustomerEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<CustomerEntity, String> {

    Boolean existsByUserEntityUserName(String userName);
    Optional<CustomerEntity> findByUserEntityUserName(String customer);
}
