package com.preethisri.retailapp.Repository;

import com.preethisri.retailapp.Entity.User;
import com.preethisri.retailapp.Enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    List<User> findByFirstName(String fName);

    List<User> findByRole(UserRole role);
}
