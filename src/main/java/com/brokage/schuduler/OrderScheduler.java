package com.brokage.schuduler;

import com.brokage.constant.OrderStatus;
import com.brokage.exception.BadRequestException;
import com.brokage.modal.OrderEntity;
import com.brokage.repository.OrderRepository;
import com.brokage.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderService orderService;
    private final OrderRepository orderRepository;


    @Scheduled(fixedRate = 1000)
    @Transactional
    public void completeOrder() {
        List<OrderEntity> orders = orderRepository.findTop50ByStatus(OrderStatus.PENDING);
        for (OrderEntity order : orders) {
            try {
                orderService.completeOrder(order);
                if (order.getStatus().equals(OrderStatus.PENDING)) {
                    order.setStatus(OrderStatus.CANCELED);
                }
            } catch (BadRequestException e) {
                order.setStatus(OrderStatus.CANCELED);
            }
        }
    }
}
