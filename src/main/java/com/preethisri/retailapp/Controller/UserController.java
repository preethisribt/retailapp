package com.preethisri.retailapp.Controller;

import com.preethisri.retailapp.DTO.Request.User.UserDTORequest;
import com.preethisri.retailapp.DTO.Response.User.UserDTOResponse;
import com.preethisri.retailapp.Enums.UserRole;
import com.preethisri.retailapp.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("api/users")
@Tag(
        name = "Users",
        description = "APIs for managing retail users"
)
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get all users",
            description = "Retrieves all users from the retail inventory."
    )
    @GetMapping
    public ResponseEntity<List<UserDTOResponse>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user by their unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserDTOResponse> getUsersById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(
            summary = "Search users",
            description = "Searches for users using one or more optional criteria such as email, first name, role, or phone number."
    )

    @GetMapping("/search")
    public ResponseEntity<List<UserDTOResponse>> getUsersBySearch(@RequestParam(required = false) @Pattern(
                                                                          regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                                                                          message = "Invalid format"
                                                                  ) String email,
                                                                  @RequestParam(required = false) String firstName,
                                                                  @RequestParam(required = false) UserRole role,
                                                                  @RequestParam(required = false) @Pattern(regexp = "^\\+?[0-9]{10,13}$", message = "Invalid") String phoneNumber) {
        return ResponseEntity.ok(userService.searchUsers(email, firstName, role, phoneNumber));
    }

    @Operation(
            summary = "Create a user",
            description = "Creates a new user."
    )
    @PostMapping
    public ResponseEntity<UserDTOResponse> createUser(@Valid @RequestBody UserDTORequest body) {
        return ResponseEntity.ok(userService.createUser(body));
    }

}
