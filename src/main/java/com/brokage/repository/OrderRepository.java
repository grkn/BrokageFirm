package com.brokage.repository;

import com.brokage.constant.OrderStatus;
import com.brokage.modal.OrderEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<OrderEntity, String> {

    List<OrderEntity> findByCreatedDateBetweenAndCustomerUserEntityUserName(LocalDateTime start, LocalDateTime end,
                                                                            String customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<OrderEntity> findTop50ByStatus(OrderStatus status);

    Optional<OrderEntity> findByIdAndCustomerId(String orderId, String customerId);

    List<OrderEntity> findByCustomerUserEntityUserName(String customerId);
}
