package com.brokage.service;

import com.brokage.constant.Currency;
import com.brokage.constant.OrderStatus;
import com.brokage.constant.SideStatus;
import com.brokage.exception.NotFoundException;
import com.brokage.modal.AssetEntity;
import com.brokage.modal.CustomerEntity;
import com.brokage.modal.OrderEntity;
import com.brokage.repository.AssetRepository;
import com.brokage.repository.CustomerRepository;
import com.brokage.repository.EmployeeRepository;
import com.brokage.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;

    @Transactional
    public OrderEntity createOrder(OrderEntity order, String customerId) {
        if (employeeRepository.existsByUserEntityUserName(customerId) ||
                (customerRepository.existsByUserEntityUserName(customerId) && customerId.equals(order.getCustomer().getId()))) {
            CustomerEntity customer = customerRepository.findByUserEntityUserName(order.getCustomer().getId())
                    .orElseThrow(() -> new NotFoundException("Customer can not be found with given customer attribute"));
            order.setCustomer(customer);
            customer.getOrderEntity().add(order);
            return orderRepository.save(order);
        }
        throw new AccessDeniedException("You are not allowed to modify other customers if you are customer");

    }

    public List<OrderEntity> findOrderByStartDateToEndDateAndCustomerName(String searchCustomer, LocalDateTime start, LocalDateTime end, String customerId) {
        if (employeeRepository.existsByUserEntityUserName(customerId) ||
                (searchCustomer.equals(customerId) && customerRepository.existsByUserEntityUserName(customerId))) {
            if (start == null || end == null) {
                return orderRepository.findByCustomerUserEntityUserName(searchCustomer);
            } else {
                return orderRepository.findByCreatedDateBetweenAndCustomerUserEntityUserName(start, end, searchCustomer);
            }
        }

        throw new AccessDeniedException("You are not allowed to modify other customers if you are customer");
    }

    public void deleteOrderByIdAndCustomerId(String orderId, String customerId) {
        if (employeeRepository.existsByUserEntityUserName(customerId)) {
            orderRepository.findById(orderId).ifPresent(orderEntity -> {
                if (orderEntity.getStatus().equals(OrderStatus.PENDING)) {
                    orderEntity.setStatus(OrderStatus.CANCELED);
                }
                orderRepository.save(orderEntity);
            });
        } else if (customerRepository.existsByUserEntityUserName(customerId)) {
            orderRepository.findByIdAndCustomerId(orderId, customerId).ifPresent(orderEntity -> {
                if (orderEntity.getStatus().equals(OrderStatus.PENDING)) {
                    orderEntity.setStatus(OrderStatus.CANCELED);
                }
                orderRepository.save(orderEntity);
            });
        }
    }

    public OrderEntity matchPendingOrderById(String orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order can not be found with given id"));
        if (orderEntity.getStatus().equals(OrderStatus.PENDING)) {
            orderEntity.setStatus(OrderStatus.MATCHED);
        }
        return orderRepository.save(orderEntity);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Retryable(retryFor = Throwable.class)
    public void completeOrder(OrderEntity order) {
        BigDecimal amount = order.getCustomer().getTryAmount();
        BigDecimal tryPrice = order.getPrice().multiply(BigDecimal.valueOf(Currency.TRY.getRatio()));
        //I just ignored usableSize because size and usableSize. It is not mentioned in this case study.
        Integer totalSize = order.getCustomer().getAssets().stream()
                .map(AssetEntity::getSize)
                .mapToInt(Integer::intValue)
                .sum();
        if (order.getOrderSide().equals(SideStatus.BUY)) {
            if (amount != null && amount.compareTo(order.getPrice().multiply(tryPrice)) < 0) {
                buyOrder(order, amount, tryPrice, totalSize);
            } else {
                LOGGER.debug(String.format("%s dont have any enough asset to buy.", order.getCustomer().getTryAmount()));
            }
        } else if (order.getOrderSide().equals(SideStatus.SELL)) {
            if (totalSize > order.getSize()) {
                sellOrder(order, amount, tryPrice, totalSize);
            } else {
                LOGGER.debug(String.format("%s dont have any enough asset to sell.", order.getCustomer().getTryAmount()));
            }
        }
    }

    private void sellOrder(OrderEntity order, BigDecimal amount, BigDecimal tryPrice, Integer totalSize) {
        order.getCustomer().setTryAmount(amount.add(tryPrice));
        order.setStatus(OrderStatus.MATCHED);

        AssetEntity asset = assetRepository.save(AssetEntity.builder()
                .assetName(order.getAssetName())
                .size(totalSize - order.getSize())
                .usableSize(totalSize - order.getSize())
                .customer(order.getCustomer())
                .build());
        order.getCustomer().getAssets().add(asset);
        customerRepository.save(order.getCustomer());
        orderRepository.save(order);
    }

    private void buyOrder(OrderEntity order, BigDecimal amount, BigDecimal tryPrice, Integer totalSize) {
        order.getCustomer().setTryAmount(amount.subtract(tryPrice));
        order.setStatus(OrderStatus.MATCHED);
        AssetEntity asset = assetRepository.save(AssetEntity.builder()
                .assetName(order.getAssetName())
                .size(totalSize + order.getSize())
                .usableSize(totalSize + order.getSize())
                .customer(order.getCustomer())
                .build());
        order.getCustomer().getAssets().add(asset);
        customerRepository.save(order.getCustomer());
        orderRepository.save(order);
    }
}