package com.datatrans.ecommerce.orderms.order;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findAllByStatusId(Integer statusId);
  Optional<Order> findByIdAndStatusId(Long id, Integer statusId);
  boolean existsByOrderNumberAndStatusId(String orderNumber, Integer statusId);
  boolean existsByOrderNumberAndIdNotAndStatusId(String orderNumber, Long id, Integer statusId);
}