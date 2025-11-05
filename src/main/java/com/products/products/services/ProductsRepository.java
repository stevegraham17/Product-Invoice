package com.products.products.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.products.products.models.Product;

	public interface ProductsRepository extends JpaRepository<Product,Long>{
		List<Product> findByCompanyId(Long companyId);
	
		List<Product> findByNameContainingIgnoreCaseAndCompanyId(String name, Long id);
		Optional<Product> findByBarcode(String barcode);
	
	}
