package com.brokage.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.brokage.constant.ChallengeConstant;
import com.brokage.exception.BadRequestException;
import com.brokage.exception.NotFoundException;
import com.brokage.modal.AuthorizationEntity;
import com.brokage.modal.CustomerEntity;
import com.brokage.modal.EmployeeEntity;
import com.brokage.modal.UserEntity;
import com.brokage.repository.CustomerRepository;
import com.brokage.repository.EmployeeRepository;
import com.brokage.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    public UserEntity findUserByName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException(String.format("User can not be found by given username : %s",
                        userName)));
    }

    @Transactional
    public UserEntity createUserEntity(UserEntity userEntity, String type) {
        LOGGER.trace("Create user request is received for user: {} ", userEntity.getUserName());
        if (userRepository.findByUserName(userEntity.getUserName()).isPresent()) {
            throw new BadRequestException("User already exists");
        }

        AuthorizationEntity authorizationEntity = new AuthorizationEntity();

        if ("Employee".equals(type)) {
            authorizationEntity.setAuth(ChallengeConstant.ROLE_ADMIN);
            EmployeeEntity employee = EmployeeEntity.builder()
                    .userEntity(userEntity)
                    .build();
            employeeRepository.save(employee);
            userEntity.setEmployeeEntity(employee);
        } else if ("Customer".equals(type)) {
            authorizationEntity.setAuth(ChallengeConstant.ROLE_CUSTOMER);
            CustomerEntity customer = CustomerEntity.builder().userEntity(userEntity).assets(List.of()).build();
            customerRepository.save(customer);
            userEntity.setCustomerEntity(customer);
        } else {
            throw new NotFoundException("Type must be Customer or Employee and it is case sensitive");
        }
        authorizationEntity.setUsers(Set.of(userEntity));

        userEntity.setAuthorizations(Set.of(authorizationEntity));
        String password = passwordEncoder.encode(userEntity.getPassword());
        userEntity.setPassword(password);
        userEntity.setUserName(userEntity.getUserName());

        userEntity = userRepository.save(userEntity);
        LOGGER.trace("Create admin(employee) user request is finished for user: {} ", userEntity.getUserName());
        return userEntity;
    }

    public String createToken(UserEntity userEntity) {
        LOGGER.trace("Create access token request is received for user: {} ", userEntity.getUserName());

        UserEntity persistedUser = findUserByName(userEntity.getUserName());

        if (passwordEncoder.matches(userEntity.getPassword(), persistedUser.getPassword())) {
            LOGGER.trace("Create access token request is finished for user: {} ", userEntity.getUserName());
            return JWT.create()
                    .withSubject(persistedUser.getUserName())
                    .withExpiresAt(new Date(System.currentTimeMillis() + ChallengeConstant.EXPIRE_TIME))
                    .sign(Algorithm.HMAC512(ChallengeConstant.DUMMY_SIGN.getBytes()));
        }

        throw new NotFoundException("Username and password are not valid");
    }
}