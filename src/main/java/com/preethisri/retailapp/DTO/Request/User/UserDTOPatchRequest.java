package com.preethisri.retailapp.DTO.Request.User;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTOPatchRequest {
    @Size(max = 100)
    @Pattern(regexp = "^[a-zA-Z]+(?: [a-zA-Z]+(?:'[a-zA-Z]+)?)*$",message = "Invalid Firstname")
    private String firstName;

    @Size(max = 100)
    @Pattern(regexp = "^[a-zA-Z]+(?: [a-zA-Z]+(?:'[a-zA-Z]+)?)*$",message = "Invalid Lastname")
    private String lastName;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Invalid email format")
    private String email;

    @Size(min = 8, max = 13, message = "Password must contain 8 to 13 characters")
    private String password;

    @Pattern(regexp = "^\\+?[0-9]{10,13}$", message = "Phone number must contain 10 to 13 digits and may start with +")
    private String phoneNumber;
}
