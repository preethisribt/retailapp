package com.preethisri.retailapp.Service;

import com.preethisri.retailapp.DTO.Request.User.UserDTORequest;
import com.preethisri.retailapp.DTO.Response.User.UserDTOResponse;
import com.preethisri.retailapp.Entity.User;
import com.preethisri.retailapp.Enums.UserRole;
import com.preethisri.retailapp.Exception.BadRequestException;
import com.preethisri.retailapp.Exception.ResourceAlreadyExistsException;
import com.preethisri.retailapp.Exception.ResourceNotFoundException;
import com.preethisri.retailapp.Mapper.UserMapper;
import com.preethisri.retailapp.Repository.UserRepository;
import com.preethisri.retailapp.Specifications.UserSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserDTOResponse> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public UserDTOResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("User not found with id: {}", id);
            throw new ResourceNotFoundException("User not found with id: " + id);
        });

        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTOResponse> searchUsers(String email, String firstName, UserRole role, String phoneNumber) {
        Specification<User> spec = null;

        if (email != null && !email.isBlank()) {
            spec = UserSpecification.hasEmail(email);
        }

        if (firstName != null && !firstName.isBlank()) {
            spec = spec == null
                    ? UserSpecification.hasFirstName(firstName)
                    : spec.and(UserSpecification.hasFirstName(firstName));
        }

        if (role != null) {
            spec = spec == null
                    ? UserSpecification.hasRole(role)
                    : spec.and(UserSpecification.hasRole(role));
        }

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            spec = spec == null
                    ? UserSpecification.hasPhoneNumber(phoneNumber)
                    : spec.and(UserSpecification.hasPhoneNumber(phoneNumber));
        }

        if (spec == null) {
            throw new BadRequestException("At least one search criteria is required");
        }

        return userRepository.findAll(spec).stream().map(userMapper::toDTO).toList();
    }

    @Transactional
    public UserDTOResponse createUser(@Valid UserDTORequest request) {
        validateDuplicateEmail(request.getEmail());
        validateDuplicatePhone(request.getPhoneNumber());

        User user = userMapper.toEntity(request);
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        user.setPassword(encodedPassword);
        user.setRole(UserRole.CUSTOMER);

        return userMapper.toDTO(userRepository.save(user));
    }

    public void validateDuplicatePhone(String phoneNumber) {
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            log.warn("Phone number already exist {}", phoneNumber);
            throw new ResourceAlreadyExistsException("User already exists with the Phone number: " + phoneNumber);
        }
    }

    public void validateDuplicateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Email already exist {}", email);
            throw new ResourceAlreadyExistsException("User already exists with the email: " + email);
        }
    }
}
