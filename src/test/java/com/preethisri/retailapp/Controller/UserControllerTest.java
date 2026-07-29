package com.preethisri.retailapp.Controller;

import com.preethisri.retailapp.DTO.Response.User.UserDTOResponse;
import com.preethisri.retailapp.Enums.UserRole;
import com.preethisri.retailapp.Exception.BadRequestException;
import com.preethisri.retailapp.Exception.ResourceNotFoundException;
import com.preethisri.retailapp.Service.UserService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;
    private UserDTOResponse userDTOResponse;

    @BeforeEach
    public void setup() {
        userDTOResponse = new UserDTOResponse();
        userDTOResponse.setId(1L);
        userDTOResponse.setEmail("michael.brown@gmail.com");
        userDTOResponse.setFirstName("Michael");
        userDTOResponse.setLastName("Brown");
        userDTOResponse.setPhoneNumber("0434567890");
        userDTOResponse.setRole(UserRole.CUSTOMER);
    }

    @Test
    public void shouldReturnAllUsers() throws Exception {
        Mockito.when(userService.getUsers()).thenReturn(List.of(userDTOResponse));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Michael"));

        Mockito.verify(userService, times(1)).getUsers();
    }

    @Test
    public void shouldReturnEmptyList_WhenNoUserExist() throws Exception {
        Mockito.when(userService.getUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        Mockito.verify(userService, times(1)).getUsers();
    }

    @Test
    public void shouldReturnUser_WhenIdIsValid() throws Exception {
        Long id = 1L;
        Mockito.when(userService.getUserById(id)).thenReturn(userDTOResponse);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        Mockito.verify(userService, times(1)).getUserById(id);
    }

    @Test
    public void shouldThrowException_WhenUserNotFound() throws Exception {
        Long id = 1111L;
        Mockito.when(userService.getUserById(id)).thenThrow(new ResourceNotFoundException("User not found with id " + id));

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id " + id));

        Mockito.verify(userService, times(1)).getUserById(id);
    }

    @Test
    public void shouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        Long id = -10L;

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("id must be greater than or equal to 1"));

        verifyNoInteractions(userService);
    }

    @Test
    public void shouldReturnUsersForSearch() throws Exception {
        String email = "michael.brown@gmail.com";
        String firstName = "Michael";
        UserRole role = UserRole.CUSTOMER;
        String phoneNumber = "0434567890";

        Mockito.when(userService.searchUsers(email, firstName, role, phoneNumber)).thenReturn(List.of(userDTOResponse));

        mockMvc.perform(get("/api/users/search").param("firstName", firstName)
                        .param("email", email)
                        .param("phoneNumber", phoneNumber)
                        .param("role", role.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value(firstName))
                .andExpect(jsonPath("$[0].email").value(email))
                .andExpect(jsonPath("$[0].lastName").value("Brown"))
                .andExpect(jsonPath("$[0].phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$[0].role").value("CUSTOMER"));

        Mockito.verify(userService, times(1)).searchUsers(email, firstName, role, phoneNumber);
    }

    @Test
    public void shouldNotReturnUsers_Search() throws Exception {
        String firstName = "Zac";
        Mockito.when(userService.searchUsers(null, firstName, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/users/search?firstName={firstName}", firstName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        Mockito.verify(userService, times(1)).searchUsers(null, firstName, null, null);
    }

    @Test
    public void shouldReturnBadRequestForInvalidPhoneNumber_Search() throws Exception {
        String phoneNumber = "123456789";

        mockMvc.perform(get("/api/users/search?phoneNumber={phoneNumber}", phoneNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("phoneNumber Invalid"));

        verifyNoInteractions(userService);
    }

    @Test
    public void shouldReturnBadRequestForInvalidEmail_Search() throws Exception {
        String email = "michael.brown";

        mockMvc.perform(get("/api/users/search?email={email}", email))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("email Invalid format"));

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnBadRequest_WhenRoleIsInvalid_Search() throws Exception {

        mockMvc.perform(get("/api/users/search")
                        .param("role", "TEST"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnBadRequest_WhenAllParamNull_Search() throws Exception {
        Mockito.when(userService.searchUsers(null, null, null, null)).thenThrow(new BadRequestException("At least one search criteria is required"));

        mockMvc.perform(get("/api/users/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("At least one search criteria is required"));

        Mockito.verify(userService, times(1)).searchUsers(null, null, null, null);

    }
}
