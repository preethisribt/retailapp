package com.preethisri.retailapp.Repository;

import com.preethisri.retailapp.DTO.Response.ProductDTOResponse;
import com.preethisri.retailapp.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryContainingIgnoreCase(String name);
    List<Product> findByProductNameContainingIgnoreCase(String name);

    boolean existsByProductNameAndCategoryAndColourAndStorageAndPrice(String productName, String category, String colour, String storage, BigDecimal price);
}

