package com.preethisri.retailapp.Repository;

import com.preethisri.retailapp.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryContainingIgnoreCase(String name);

    @Query("""
                SELECT p FROM Product p
            WHERE LOWER(p.productName)
            LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<Product> searchProducts(@Param("keyword") String keyword);
}

