package com.brokage.controller;

import com.brokage.constant.ChallengeConstant;
import com.brokage.constant.OrderStatus;
import com.brokage.dto.OrderDto;
import com.brokage.modal.CustomerEntity;
import com.brokage.modal.OrderEntity;
import com.brokage.resource.OrderResource;
import com.brokage.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = ChallengeConstant.BASE_URL + "/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<OrderResource> createOrder(@RequestBody @Valid OrderDto orderDto) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        CustomerEntity customer = CustomerEntity.builder().build();
        customer.setId(orderDto.getCustomer());
        OrderEntity orderEntity = orderService.createOrder(OrderEntity.builder()
                .orderSide(orderDto.getSide())
                .price(orderDto.getPrice())
                .createdDate(LocalDateTime.now())
                .assetName(orderDto.getAsset())
                .size(orderDto.getSize())
                .status(OrderStatus.PENDING)
                .customer(customer)
                .build(), userName);
        return ResponseEntity.created(URI.create("/order/" + orderEntity.getId()))
                .body(OrderResource.builder().orderId(orderEntity.getId()).build());
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        orderService.deleteOrderByIdAndCustomerId(orderId, userName);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{customer}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<OrderResource>> getOrders(@PathVariable String customer, @RequestBody OrderDto orderDto) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<OrderEntity> orders = orderService.findOrderByStartDateToEndDateAndCustomerName(customer, orderDto.getStart(), orderDto.getEnd(), userName);
        return ResponseEntity.ok(orders.stream().map(order -> OrderResource.builder()
                        .orderId(order.getId())
                        .asset(order.getAssetName())
                        .side(order.getOrderSide())
                        .size(order.getSize())
                        .price(order.getPrice())
                        .customer(order.getCustomer().getUserEntity().getUserName())
                        .createDate(order.getCreatedDate())
                        .status(order.getStatus())
                        .build())
                .collect(Collectors.toList()));
    }

    @PutMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<OrderResource> matchPendingOrders(@PathVariable String orderId) {
        OrderEntity order = orderService.matchPendingOrderById(orderId);

        return ResponseEntity.ok(OrderResource.builder()
                .orderId(order.getId())
                .asset(order.getAssetName())
                .side(order.getOrderSide())
                .size(order.getSize())
                .price(order.getPrice())
                .customer(order.getCustomer().getUserEntity().getUserName())
                .createDate(order.getCreatedDate())
                .status(order.getStatus())
                .build());
    }

}
