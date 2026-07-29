package com.preethisri.retailapp.Specifications;

import com.preethisri.retailapp.Entity.User;
import com.preethisri.retailapp.Enums.UserRole;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("email"), email);
    }

    public static Specification<User> hasPhoneNumber(String phoneNumber) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("phoneNumber"), phoneNumber);
    }

    public static Specification<User> hasRole(UserRole role) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("role"), role);
    }
}
