package com.products.products.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.products.products.models.SignedNotification;

import java.util.List;



public interface NotificationRepository extends JpaRepository<SignedNotification, Long> {
	 List<SignedNotification> findByUsernameAndIsReadFalseOrderByCreatedAtDesc(String username);
    
}
