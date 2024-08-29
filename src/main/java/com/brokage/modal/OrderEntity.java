package com.brokage.modal;

import com.brokage.constant.OrderStatus;
import com.brokage.constant.SideStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class OrderEntity extends BaseEntity {

    @ManyToOne
    private CustomerEntity customer;
    private String assetName;
    @Enumerated(EnumType.STRING)
    private SideStatus orderSide;
    private Integer size;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime createdDate;
}
