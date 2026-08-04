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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private UserDTOResponse userDTOResponse;
    private UserDTORequest userDTORequest;

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

        userDTORequest = new UserDTORequest();
        userDTORequest.setEmail("michael.brown@gmail.com");
        userDTORequest.setFirstName("Michael");
        userDTORequest.setLastName("Brown");
        userDTORequest.setPhoneNumber("0434567890");
        userDTORequest.setPassword("michael6844");
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

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        Mockito.when(userRepository.findByPhoneNumber(userDTORequest.getPhoneNumber())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(userDTORequest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(userDTORequest.getPassword())).thenReturn("encodedPassword");

        Mockito.when(userMapper.toEntity(userDTORequest)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toDTO(user)).thenReturn(userDTOResponse);

        UserDTOResponse response = userService.createUser(userDTORequest);
        Assertions.assertEquals("encodedPassword", user.getPassword());
        Assertions.assertEquals(1L, response.getId());
        Assertions.assertEquals("Michael", response.getFirstName());
        Assertions.assertEquals(UserRole.CUSTOMER, user.getRole());

        Mockito.verify(userRepository).findByEmail(userDTORequest.getEmail());
        Mockito.verify(userRepository).findByPhoneNumber(userDTORequest.getPhoneNumber());
        Mockito.verify(passwordEncoder).encode(userDTORequest.getPassword());
        Mockito.verify(userRepository).save(user);
        Mockito.verify(userMapper).toEntity(userDTORequest);
        Mockito.verify(userMapper).toDTO(user);
    }

    @Test
    void shouldReturnConflict_WhenEmailAlreadyExists() throws Exception {
        Mockito.when(userRepository.findByEmail(userDTORequest.getEmail())).thenReturn(Optional.of(user));

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> userService.createUser(userDTORequest));
        Mockito.verify(userRepository).findByEmail(userDTORequest.getEmail());
        Mockito.verifyNoInteractions(userMapper);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldReturnConflict_WhenPhoneAlreadyExists() throws Exception {
        Mockito.when(userRepository.findByEmail(userDTORequest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByPhoneNumber(userDTORequest.getPhoneNumber())).thenReturn(Optional.of(user));

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> userService.createUser(userDTORequest));
        Mockito.verify(userRepository).findByEmail(userDTORequest.getEmail());
        Mockito.verify(userRepository).findByPhoneNumber(userDTORequest.getPhoneNumber());
        Mockito.verifyNoInteractions(userMapper);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldAbleToUpdateUser() {
        User userExisting = new User();
        userExisting.setId(1L);
        userExisting.setFirstName("Michael");
        userExisting.setEmail("michael.brown123@gmail.com");
        userExisting.setPhoneNumber("0434511190");
        userExisting.setPassword("oldEncodedPassword");


        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userExisting));
        Mockito.when(passwordEncoder.matches(userDTORequest.getPassword(), userExisting.getPassword())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(userDTORequest.getPassword())).thenReturn("encodedPassword");
        Mockito.when(userRepository.findByEmail(userDTORequest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByPhoneNumber(userDTORequest.getPhoneNumber())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(userExisting)).thenReturn(user);
        Mockito.when(userMapper.toDTO(user)).thenReturn(userDTOResponse);


        UserDTOResponse response = userService.updateUser(1L, userDTORequest);
        Assertions.assertEquals(1L, response.getId());

        Assertions.assertEquals("Michael", response.getFirstName());
        Assertions.assertEquals("michael.brown@gmail.com", response.getEmail());
        Assertions.assertEquals("0434567890", response.getPhoneNumber());

        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(userRepository).findByEmail(userDTORequest.getEmail());
        Mockito.verify(userRepository).findByPhoneNumber(userDTORequest.getPhoneNumber());
        Mockito.verify(passwordEncoder).encode(userDTORequest.getPassword());
        Mockito.verify(passwordEncoder).matches(userDTORequest.getPassword(), "oldEncodedPassword");
        Mockito.verify(userRepository).save(userExisting);
        Mockito.verify(userMapper).toDTO(user);
    }

    @Test
    void shouldReturnResourceAlreadyExist_DuplicateEmail_UpdateUser() {
        User userExisting = new User();
        userExisting.setId(1L);
        userExisting.setFirstName("Michael");
        userExisting.setEmail("michael.brown123@gmail.com");
        userExisting.setPhoneNumber("0434511190");
        userExisting.setPassword("oldEncodedPassword");

        User user2 = new User();
        user2.setEmail("michael.brown@gmail.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userExisting));
        Mockito.when(passwordEncoder.matches(userDTORequest.getPassword(), userExisting.getPassword())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(userDTORequest.getPassword())).thenReturn("encodedPassword");
        Mockito.when(userRepository.findByEmail(userDTORequest.getEmail())).thenReturn(Optional.of(user2));

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(1L, userDTORequest));

        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(passwordEncoder).encode(userDTORequest.getPassword());
        Mockito.verify(passwordEncoder).matches(userDTORequest.getPassword(), "oldEncodedPassword");
        Mockito.verify(userRepository).findByEmail(userDTORequest.getEmail());
        Mockito.verify(userRepository, Mockito.times(0)).save(userExisting);
        Mockito.verify(userMapper, Mockito.never()).toDTO(Mockito.any(User.class));
    }


    @Test
    void shouldReturnResourceAlreadyExist_DuplicatePhone_UpdateUser() {
        User userExisting = new User();
        userExisting.setId(1L);
        userExisting.setFirstName("Michael");
        userExisting.setEmail("michael.brown123@gmail.com");
        userExisting.setPhoneNumber("0434511190");
        userExisting.setPassword("oldEncodedPassword");

        User user2 = new User();
        user2.setPhoneNumber("0434567890");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userExisting));
        Mockito.when(passwordEncoder.matches(userDTORequest.getPassword(), userExisting.getPassword())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(userDTORequest.getPassword())).thenReturn("encodedPassword");
        Mockito.when(userRepository.findByEmail(userDTORequest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByPhoneNumber(userDTORequest.getPhoneNumber())).thenReturn(Optional.of(user2));

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(1L, userDTORequest));

        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(passwordEncoder).encode(userDTORequest.getPassword());
        Mockito.verify(passwordEncoder).matches(userDTORequest.getPassword(), "oldEncodedPassword");
        Mockito.verify(userRepository).findByEmail(userDTORequest.getEmail());
        Mockito.verify(userRepository).findByPhoneNumber(userDTORequest.getPhoneNumber());
        Mockito.verify(userRepository, Mockito.never()).save(userExisting);
        Mockito.verifyNoInteractions(userMapper);
    }

    @Test
    void shouldReturnResourceNotFoundException_InvalidId_UpdateUser() {
        Long id = 111L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(id, userDTORequest));

        Mockito.verify(userRepository).findById(id);
        Mockito.verifyNoInteractions(userMapper);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldNotEncodePasswordWhenRequestIsSame_UpdateUser() {
        User userExisting = new User();
        userExisting.setId(1L);
        userExisting.setEmail("michael.brown@gmail.com");
        userExisting.setFirstName("Michael");
        userExisting.setLastName("Brown");
        userExisting.setPhoneNumber("0434567890");
        userExisting.setRole(UserRole.CUSTOMER);
        userExisting.setPassword("encodedPassword");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userExisting));
        Mockito.when(passwordEncoder.matches(userDTORequest.getPassword(), userExisting.getPassword())).thenReturn(true);
        Mockito.when(userRepository.save(userExisting)).thenReturn(user);
        Mockito.when(userMapper.toDTO(user)).thenReturn(userDTOResponse);

        UserDTOResponse response = userService.updateUser(1L, userDTORequest);
        Assertions.assertEquals(1L, response.getId());
        Assertions.assertEquals("Michael", response.getFirstName());
        Assertions.assertEquals("michael.brown@gmail.com", response.getEmail());
        Assertions.assertEquals("0434567890", response.getPhoneNumber());
        Assertions.assertEquals("encodedPassword", userExisting.getPassword());

        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(passwordEncoder).matches(userDTORequest.getPassword(), "encodedPassword");
        Mockito.verify(userRepository).save(userExisting);
        Mockito.verify(userMapper).toDTO(user);

        Mockito.verify(userRepository,Mockito.never()).findByEmail(userDTORequest.getEmail());
        Mockito.verify(userRepository,Mockito.never()).findByPhoneNumber(userDTORequest.getPhoneNumber());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(userDTORequest.getPassword());
    }
}