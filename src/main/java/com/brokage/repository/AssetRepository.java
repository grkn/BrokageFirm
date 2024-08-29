package com.brokage.repository;

import com.brokage.modal.AssetEntity;
import org.springframework.data.repository.CrudRepository;

public interface AssetRepository extends CrudRepository<AssetEntity, String> {
}
