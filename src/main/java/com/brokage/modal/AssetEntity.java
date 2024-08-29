package com.brokage.modal;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetEntity extends BaseEntity {

    @ManyToOne
    private CustomerEntity customer;
    private String assetName;
    private Integer size;
    private Integer usableSize;
}
