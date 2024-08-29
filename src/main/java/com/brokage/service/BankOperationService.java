package com.brokage.service;

import com.brokage.constant.Currency;
import com.brokage.exception.BadRequestException;
import com.brokage.exception.NotFoundException;
import com.brokage.modal.CustomerEntity;
import com.brokage.repository.CustomerRepository;
import com.brokage.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BankOperationService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    public BigDecimal depositAmount(BigDecimal amount, String tokenName, String customerName) {
        if (employeeRepository.existsByUserEntityUserName(tokenName) ||
                customerRepository.existsByUserEntityUserName(customerName) && tokenName.equals(customerName)) {
            CustomerEntity customer = customerRepository.findByUserEntityUserName(customerName)
                    .orElseThrow(() -> new NotFoundException("Customer can not be found with customerName"));

            BigDecimal currentAmount = customer.getTryAmount() == null ?
                    BigDecimal.ZERO.add(amount.multiply(BigDecimal.valueOf(Currency.TRY.getRatio())))
                    : customer.getTryAmount().add(amount.multiply(BigDecimal.valueOf(Currency.TRY.getRatio())));
            customer.setTryAmount(currentAmount);
            customerRepository.save(customer);
            return currentAmount;
        }
        throw new AccessDeniedException("You are not allowed to modify deposit amount");
    }

    public BigDecimal withdrawAmount(BigDecimal amount, String tokenName, String customerName) {

        if (employeeRepository.existsByUserEntityUserName(tokenName) ||
                (customerRepository.existsByUserEntityUserName(customerName) && tokenName.equals(customerName))) {
            CustomerEntity customer = customerRepository.findByUserEntityUserName(customerName)
                    .orElseThrow(() -> new NotFoundException("Customer can not be found with customerName"));
            if (customer.getTryAmount() != null && customer.getTryAmount().compareTo(amount) > 0) {
                BigDecimal currentAmount = customer.getTryAmount()
                        .subtract(amount.multiply(BigDecimal.valueOf(Currency.TRY.getRatio())));
                customer.setTryAmount(currentAmount);
                customerRepository.save(customer);
                return currentAmount;
            } else {
                throw new BadRequestException("Your amount is not suffient to withdraw money");
            }
        }
        // LOG and ignore as debug
        throw new AccessDeniedException("You are not allowed to modify withdraw amount");
    }

}
