package com.datatrans.ecommerce.productms.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // active-only queries
    List<Product> findAllByStatusId(Integer statusId);
    Optional<Product> findByIdAndStatusId(Long id, Integer statusId);

    // uniqueness among active rows
    boolean existsBySkuAndStatusId(String sku, Integer statusId);
    boolean existsBySkuAndIdNotAndStatusId(String sku, Long id, Integer statusId);
}
