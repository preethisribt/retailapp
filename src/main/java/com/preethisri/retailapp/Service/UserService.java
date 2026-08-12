package com.preethisri.retailapp.Service;

import com.preethisri.retailapp.DTO.Request.User.UserDTOPatchRequest;
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
        return userMapper.toDTO(findUserById(id));
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.warn("User not found with id: {}", id);
            throw new ResourceNotFoundException("User not found with id: " + id);
        });
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
    public UserDTOResponse createUser(UserDTORequest request) {
        validateDuplicateEmail(request.getEmail());
        validateDuplicatePhone(request.getPhoneNumber());

        User user = userMapper.toEntity(request);
        encodeAndSetPassword(user, request.getPassword());
        user.setRole(UserRole.CUSTOMER);

        return userMapper.toDTO(userRepository.save(user));
    }

    private void encodeAndSetPassword(User user, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);

        log.debug(encodedPassword + " updated for {id}", user.getId());
    }

    private void validateDuplicatePhone(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
                log.warn("Phone number already exist {}", phoneNumber);
                throw new ResourceAlreadyExistsException("User already exists with the Phone number: " + phoneNumber);
            }
        }
    }

    private void validateDuplicateEmail(String email) {
        if (email != null && !email.isBlank()) {
            if (userRepository.findByEmail(email).isPresent()) {
                log.warn("Email already exist {}", email);
                throw new ResourceAlreadyExistsException("User already exists with the email: " + email);
            }
        }
    }

    private void updatePhone(User user, String phone) {
        if (phone != null && !phone.isBlank()) {
            if (!user.getPhoneNumber().equals(phone)) {
                validateDuplicatePhone(phone);
                user.setPhoneNumber(phone);

                log.debug(phone + " updated for {id}", user.getId());
            }
        }
    }

    private void updatePassword(User user, String password) {
        if (password != null && !password.isBlank()) {
            boolean existingPassword = passwordEncoder.matches(password, user.getPassword());

            if (!existingPassword) {
                encodeAndSetPassword(user, password);
            }
        }
    }

    private void updateEmail(User user, String email) {
        if (email != null && !email.isBlank()) {
            if (!user.getEmail().equals(email)) {
                validateDuplicateEmail(email);
                user.setEmail(email);

                log.debug(email + " updated for {id}", user.getId());

            }
        }
    }

    private void updateFirstName(User user, String firstName) {
        if (firstName != null && !firstName.isBlank()) {
            user.setFirstName(firstName);

            log.debug(firstName + " updated for {id}", user.getId());
        }
    }

    private void updateLastName(User user, String lastName) {
        if (lastName != null && !lastName.isBlank()) {
            user.setLastName(lastName);

            log.debug(lastName + " updated for {id}", user.getId());
        }
    }

    @Transactional
    public UserDTOResponse updateUser(Long id, UserDTORequest request) {
        User existingUser = findUserById(id);

        updateFirstName(existingUser, request.getFirstName());
        updateLastName(existingUser, request.getLastName());
        updatePassword(existingUser, request.getPassword());
        updateEmail(existingUser, request.getEmail());
        updatePhone(existingUser, request.getPhoneNumber());

        log.info("Updated user {id}", existingUser.getId());
        return userMapper.toDTO(userRepository.save(existingUser));
    }

    @Transactional
    public UserDTOResponse partialUpdateUser(Long id, UserDTOPatchRequest request) {
        if (request.getFirstName() == null && request.getLastName() == null && request.getEmail() == null &&
                request.getPhoneNumber() == null && request.getPassword() == null) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }

        User existingUser = findUserById(id);

        updateFirstName(existingUser, request.getFirstName());
        updateLastName(existingUser, request.getLastName());
        updatePassword(existingUser, request.getPassword());
        updateEmail(existingUser, request.getEmail());
        updatePhone(existingUser, request.getPhoneNumber());

        log.info("Partial updated user {id}", existingUser.getId());
        return userMapper.toDTO(userRepository.save(existingUser));
    }
}
