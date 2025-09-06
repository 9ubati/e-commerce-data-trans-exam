package com.datatrans.ecommerce.userms.permissions;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
	
	 // Active-only reads
    List<Permission> findAllByStatusId(Integer statusId);
    Optional<Permission> findByIdAndStatusId(Long id, Integer statusId);

    // Uniqueness (active rows only)
    boolean existsByCodeAndStatusId(String code, Integer statusId);
    boolean existsByCodeAndIdNotAndStatusId(String code, Long id, Integer statusId);
    
    
    List<Permission> findAllByUserRoleAndStatusId(String userRole, Integer statusId);
	
}
