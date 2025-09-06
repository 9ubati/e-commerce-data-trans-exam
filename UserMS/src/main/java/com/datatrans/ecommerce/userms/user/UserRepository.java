package com.datatrans.ecommerce.userms.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByIdAndStatusId( Long id, Integer statusId);
	boolean existsByUsernameAndStatusId(String username, Integer statusId);
	Optional<User> findByUsernameAndStatusId(String username, Integer statusId);
	boolean existsByUsernameAndIdNotAndStatusId(String username, Long id, Integer statusId);
	boolean existsByEmailAndIdNotAndStatusId(String email, Long id, Integer statusId);
	boolean existsByMobileAndIdNotAndStatusId(String mobile, Long id, Integer statusId);
	
}
