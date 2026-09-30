package com.preethisri.retailapp.Repository;

import com.preethisri.retailapp.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
