package com.preethisri.retailapp.Service;

import com.preethisri.retailapp.DTO.Response.User.UserDTOResponse;
import com.preethisri.retailapp.Entity.User;
import com.preethisri.retailapp.Enums.UserRole;
import com.preethisri.retailapp.Exception.BadRequestException;
import com.preethisri.retailapp.Exception.ResourceNotFoundException;
import com.preethisri.retailapp.Mapper.UserMapper;
import com.preethisri.retailapp.Repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    //@Mock
    private User user;
    // @Mock
    private UserDTOResponse userDTOResponse;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("michael.brown@gmail.com");
        user.setFirstName("Michael");
        user.setLastName("Brown");
        user.setPhoneNumber("0434567890");
        user.setRole(UserRole.CUSTOMER);

        userDTOResponse = new UserDTOResponse();
        userDTOResponse.setId(1L);
        userDTOResponse.setEmail("michael.brown@gmail.com");
        userDTOResponse.setFirstName("Michael");
        userDTOResponse.setLastName("Brown");
        userDTOResponse.setPhoneNumber("0434567890");
        userDTOResponse.setRole(UserRole.CUSTOMER);
    }

    @Test
    public void shouldReturnAllUsers() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        Mockito.when(userMapper.toDTO(user)).thenReturn(userDTOResponse);

        List<UserDTOResponse> response = userService.getUsers();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(1, response.get(0).getId());
        Assertions.assertEquals("Michael", response.get(0).getFirstName());


        Mockito.verify(userRepository).findAll();
        Mockito.verify(userMapper).toDTO(user);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    public void shouldReturnEmptyList_WhenNoUserExist() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of());

        List<UserDTOResponse> response = userService.getUsers();
        Assertions.assertEquals(0, response.size());
        Assertions.assertTrue(response.isEmpty());

        Mockito.verify(userRepository).findAll();
        Mockito.verifyNoInteractions(userMapper);
    }

    @Test
    public void shouldReturnUser_WhenIdIsValid() {
        Long id = 1L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toDTO(user)).thenReturn(userDTOResponse);

        UserDTOResponse response = userService.getUserById(id);
        Assertions.assertEquals(1, response.getId());

        Mockito.verify(userRepository).findById(id);
        Mockito.verify(userMapper).toDTO(user);
    }

    @Test
    public void shouldThrowException_WhenUserNotFound() throws Exception {
        Long id = 111L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(id));

        Mockito.verify(userRepository).findById(id);
        Mockito.verifyNoInteractions(userMapper);
    }

    @Test
    public void shouldReturnUsersForSearch() throws Exception {
        String email = "michael.brown@gmail.com";
        String firstName = "Michael";
        UserRole role = UserRole.CUSTOMER;
        String phoneNumber = "0434567890";

        Mockito.when(userRepository.findAll(any(Specification.class))).thenReturn(List.of(user));
        Mockito.when(userMapper.toDTO(user)).thenReturn(userDTOResponse);

        List<UserDTOResponse> responses = userService.searchUsers(email, firstName, role, phoneNumber);
        UserDTOResponse response = responses.get(0);

        Assertions.assertEquals(firstName, response.getFirstName());
        Assertions.assertEquals(email, response.getEmail());
        Assertions.assertEquals(role, response.getRole());
        Assertions.assertEquals(phoneNumber, response.getPhoneNumber());

        Mockito.verify(userRepository).findAll(any(Specification.class));
        Mockito.verify(userMapper).toDTO(user);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    public void shouldNotReturnUsers_Search() throws Exception {
        String firstName = "Zac";

        Mockito.when(userRepository.findAll(any(Specification.class))).thenReturn(List.of());

        List<UserDTOResponse> responses = userService.searchUsers(null, firstName, null, null);
        Assertions.assertEquals(responses.size(), 0);

        Mockito.verify(userRepository).findAll(any(Specification.class));
        Mockito.verifyNoInteractions(userMapper);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldReturnBadRequest_WhenAllParamNull_Search() throws Exception {
        Assertions.assertThrows(BadRequestException.class, () -> userService.searchUsers(null, null, null, null));

        Mockito.verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void shouldReturnMultipleUsers_WhenSearchMatchesMultipleUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Michael");
        user1.setEmail("michael.brown@gmail.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Michael");
        user2.setEmail("michael.smith@gmail.com");

        UserDTOResponse dto1 = new UserDTOResponse();
        dto1.setId(1L);
        dto1.setFirstName("Michael");
        dto1.setEmail("michael.brown@gmail.com");

        UserDTOResponse dto2 = new UserDTOResponse();
        dto2.setId(2L);
        dto2.setFirstName("Michael");
        dto2.setEmail("michael.smith@gmail.com");

        Mockito.when(userRepository.findAll(any(Specification.class))).thenReturn(List.of(user1, user2));

        Mockito.when(userMapper.toDTO(user1)).thenReturn(dto1);
        Mockito.when(userMapper.toDTO(user2)).thenReturn(dto2);

        List<UserDTOResponse> responses =
                userService.searchUsers(null, "Michael", null, null);

        Assertions.assertEquals(2, responses.size());
        Assertions.assertEquals("Michael", responses.getFirst().getFirstName());
        Assertions.assertEquals("Michael", responses.getLast().getFirstName());
        Assertions.assertEquals("michael.brown@gmail.com", responses.getFirst().getEmail());
        Assertions.assertEquals("michael.smith@gmail.com", responses.getLast().getEmail());

        Mockito.verify(userRepository).findAll(any(Specification.class));
        Mockito.verify(userMapper).toDTO(user1);
        Mockito.verify(userMapper).toDTO(user2);
    }
}