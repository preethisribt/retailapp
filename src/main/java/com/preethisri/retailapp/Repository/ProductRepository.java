package com.preethisri.retailapp.Repository;

import com.preethisri.retailapp.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
