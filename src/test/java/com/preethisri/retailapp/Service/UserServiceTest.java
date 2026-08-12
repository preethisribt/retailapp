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
    private UserDTOPatchRequest userDTOPatchRequest;
    private UserDTORequest userDTORequest;
    private User userExisting = new User();

    @BeforeEach
    public void setup() {
        userExisting = new User();
        userExisting.setId(1L);
        userExisting.setFirstName("Michael");
        userExisting.setEmail("michael.brown123@gmail.com");
        userExisting.setPhoneNumber("0434511190");
        userExisting.setPassword("oldPassword");

        userDTOPatchRequest = new UserDTOPatchRequest();
        userDTOPatchRequest.setEmail("michael.brown@gmail.com");
        userDTOPatchRequest.setFirstName("Michael");
        userDTOPatchRequest.setLastName("Brown");
        userDTOPatchRequest.setPhoneNumber("0434567890");
        userDTOPatchRequest.setPassword("michael6844");

        user = new User();
        user.setId(1L);
        user.setEmail("michael.brown@gmail.com");
        user.setFirstName("Michael");
        user.setLastName("Brown");
        user.setPhoneNumber("0434567890");
        user.setPassword("OldEncodedPassword");
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
        Long id = 1L;
        String email = "michael.brown@gmail.com";
        String phoneNumber = "0434567890";
        String password = "PassWord@#@";
        String encodedPassword = "EncodedPassword";
        String firstName = "Michael Brad";
        String lastName = "Willington";

        userDTORequest.setPassword(password);
        user.setPassword(encodedPassword);
        userDTOResponse.setEmail(email);
        userDTOResponse.setFirstName(firstName);
        userDTOResponse.setLastName(lastName);
        userDTOResponse.setPhoneNumber(phoneNumber);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toDTO(user)).thenReturn(userDTOResponse);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(userDTORequest.getPassword())).thenReturn(encodedPassword);

        UserDTOResponse response = userService.updateUser(id, userDTORequest);

        Assertions.assertEquals(id, response.getId());
        Assertions.assertEquals(firstName, response.getFirstName());
        Assertions.assertEquals(lastName, response.getLastName());
        Assertions.assertEquals(phoneNumber, response.getPhoneNumber());
        Assertions.assertEquals(email, response.getEmail());
        Assertions.assertEquals(encodedPassword, user.getPassword());

        Mockito.verify(userRepository).findById(id);
        Mockito.verify(userRepository).save(user);
        Mockito.verify(userMapper).toDTO(user);
        Mockito.verify(passwordEncoder).matches(password, user.getPassword());
        Mockito.verify(passwordEncoder).encode(userDTORequest.getPassword());
    }

    @Test
    void shouldReturnResourceAlreadyExist_DuplicateEmail_UpdateUser() {
        User user2 = new User();
        user2.setEmail("michael.brown@gmail.com");

        Mockito.when(passwordEncoder.matches(userDTORequest.getPassword(), userExisting.getPassword())).thenReturn(true);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userExisting));
        Mockito.when(userRepository.findByEmail(userDTORequest.getEmail())).thenReturn(Optional.of(user2));
        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(1L, userDTORequest));

        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(userRepository).findByEmail(userDTORequest.getEmail());
        Mockito.verify(passwordEncoder).matches(any(), any());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(any());
        Mockito.verify(userRepository, Mockito.never()).save(userExisting);
        Mockito.verify(userMapper, Mockito.never()).toDTO(any(User.class));
    }


    @Test
    void shouldReturnResourceAlreadyExist_DuplicatePhone_UpdateUser() {
        User user2 = new User();
        user2.setPhoneNumber("0434567890");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userExisting));
        Mockito.when(userRepository.findByEmail(userDTORequest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByPhoneNumber(userDTORequest.getPhoneNumber())).thenReturn(Optional.of(user2));
        Mockito.when(passwordEncoder.matches(userDTORequest.getPassword(), userExisting.getPassword())).thenReturn(true);

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(1L, userDTORequest));

        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(passwordEncoder).matches(userDTORequest.getPassword(), userExisting.getPassword());
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
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(userDTORequest.getPassword(), user.getPassword())).thenReturn(true);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toDTO(user)).thenReturn(userDTOResponse);

        UserDTOResponse response = userService.updateUser(1L, userDTORequest);
        Assertions.assertEquals(1L, response.getId());
        Assertions.assertEquals("Michael", response.getFirstName());
        Assertions.assertEquals("michael.brown@gmail.com", response.getEmail());
        Assertions.assertEquals("0434567890", response.getPhoneNumber());
        Assertions.assertEquals("OldEncodedPassword", user.getPassword());

        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(passwordEncoder).matches(userDTORequest.getPassword(), user.getPassword());
        Mockito.verify(userRepository).save(user);
        Mockito.verify(userMapper).toDTO(user);
        Mockito.verify(passwordEncoder, Mockito.never()).encode(userDTORequest.getPassword());
    }

    @Test
    void shouldAbleTo_UpdateAllField_APartiallyUpdateUser() {
        Long id = 1L;
        String email = "michael.brown@gmail.com";
        String phoneNumber = "0434567890";
        String password = "PassWord@#@";
        String encodedPassword = "EncodedPassword";
        String firstName = "Michael Brad";
        String lastName = "Willington";

        userDTOPatchRequest.setEmail(email);
        userDTOPatchRequest.setFirstName(firstName);
        userDTOPatchRequest.setLastName(lastName);
        userDTOPatchRequest.setPhoneNumber(phoneNumber);
        userDTOPatchRequest.setPassword(password);
        user.setPassword(encodedPassword);

        userDTOResponse.setEmail(email);
        userDTOResponse.setFirstName(firstName);
        userDTOResponse.setLastName(lastName);
        userDTOResponse.setPhoneNumber(phoneNumber);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toDTO(user)).thenReturn(userDTOResponse);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(any(), any())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        UserDTOResponse response = userService.partialUpdateUser(id, userDTOPatchRequest);

        Assertions.assertEquals(id, response.getId());
        Assertions.assertEquals(firstName, response.getFirstName());
        Assertions.assertEquals(lastName, response.getLastName());
        Assertions.assertEquals(phoneNumber, response.getPhoneNumber());
        Assertions.assertEquals(email, response.getEmail());
        Assertions.assertEquals(encodedPassword, user.getPassword());

        Mockito.verify(userRepository).findById(id);
        Mockito.verify(userRepository).save(user);
        Mockito.verify(userMapper).toDTO(user);
        Mockito.verify(passwordEncoder).matches(any(), any());
        Mockito.verify(passwordEncoder).encode(password);
    }

    @Test
    void shouldPartiallyUpdate_NameField_UserSuccessfully() {
        Long id = 1L;
        userDTOPatchRequest.setFirstName("Michael Brad");
        userDTOPatchRequest.setLastName("");
        userDTOPatchRequest.setPassword("");
        userDTOPatchRequest.setEmail("");
        userDTOPatchRequest.setPhoneNumber("");
        userDTOResponse.setFirstName("Michael Brad");

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toDTO(user)).thenReturn(userDTOResponse);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        UserDTOResponse response = userService.partialUpdateUser(id, userDTOPatchRequest);
        Assertions.assertEquals(id, response.getId());
        Assertions.assertEquals("Michael Brad", response.getFirstName());
        Assertions.assertEquals("Brown", response.getLastName());
        Assertions.assertEquals("0434567890", response.getPhoneNumber());
        Assertions.assertEquals("michael.brown@gmail.com", response.getEmail());

        Mockito.verify(userRepository).findById(id);
        Mockito.verify(userRepository).save(user);
        Mockito.verify(userMapper).toDTO(user);
        Mockito.verify(passwordEncoder, Mockito.never()).matches(any(), any());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(any());
    }

    @Test
    void should_updatePassword_PartiallyUpdateUserSuccessfully() {
        Long id = 1L;
        String password = "@#$MichaelBrad%34";
        String encodedPassword = "encodedPassword";
        userDTOPatchRequest.setPassword(password);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toDTO(user)).thenReturn(userDTOResponse);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(any(), any())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        user.setPassword(encodedPassword);

        UserDTOResponse response = userService.partialUpdateUser(id, userDTOPatchRequest);
        Assertions.assertEquals(id, response.getId());
        Assertions.assertEquals("Michael", response.getFirstName());
        Assertions.assertEquals("Brown", response.getLastName());
        Assertions.assertEquals("0434567890", response.getPhoneNumber());
        Assertions.assertEquals("michael.brown@gmail.com", response.getEmail());
        Assertions.assertEquals(encodedPassword, user.getPassword());

        Mockito.verify(userRepository).findById(id);
        Mockito.verify(userRepository).save(user);
        Mockito.verify(userMapper).toDTO(user);
        Mockito.verify(passwordEncoder).encode(password);
        Mockito.verify(passwordEncoder).matches(any(), any());
    }

    @Test
    void shouldUpdate_partialFieldsSuccessfully_PartialUpdateUser() {
        String email = "michael.brown@gmail.com";
        String phoneNumber = "0434567890";
        String password = "PassWord@#@";
        String encodedPassword = "EncodedPassword";

        userDTOPatchRequest.setEmail(email);
        userDTOPatchRequest.setPhoneNumber(phoneNumber);
        userDTOPatchRequest.setPassword(password);

        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(encodedPassword);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(userDTOPatchRequest.getPassword(), user.getPassword())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(userDTOPatchRequest.getPassword())).thenReturn(encodedPassword);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toDTO(user)).thenReturn(userDTOResponse);

        UserDTOResponse response = userService.partialUpdateUser(1L, userDTOPatchRequest);
        Assertions.assertEquals(1L, response.getId());
        Assertions.assertEquals("Michael", response.getFirstName());
        Assertions.assertEquals("Brown", response.getLastName());
        Assertions.assertEquals(email, response.getEmail());
        Assertions.assertEquals(phoneNumber, response.getPhoneNumber());
        Assertions.assertEquals(encodedPassword, user.getPassword());

        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(passwordEncoder).matches(userDTOPatchRequest.getPassword(), user.getPassword());
        Mockito.verify(passwordEncoder).encode(userDTOPatchRequest.getPassword());
        Mockito.verify(userRepository).save(user);
        Mockito.verify(userMapper).toDTO(user);
    }

    @Test
    void shouldNotUpdatePassword_WhenSimilar() {
        Long id = 1L;
        String password = "@#$MichaelBrad%34";
        String existingPassword = "existingEncodedPassword";

        user.setPassword(existingPassword);
        userDTOPatchRequest.setPassword(password);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(password, existingPassword)).thenReturn(true);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toDTO(user)).thenReturn(userDTOResponse);

        userService.partialUpdateUser(id, userDTOPatchRequest);

        Assertions.assertEquals(existingPassword, user.getPassword());

        Mockito.verify(passwordEncoder).matches(password, existingPassword);
        Mockito.verify(passwordEncoder, Mockito.never()).encode(any());
        Mockito.verify(userRepository).save(user);
        Mockito.verify(userMapper).toDTO(user);
    }


    @Test
    void shouldReturnException_AllFieldNull_PartiallyUpdateUser() {
        Long id = 1L;
        UserDTOPatchRequest request = new UserDTOPatchRequest();

        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.partialUpdateUser(id, request));

        Mockito.verifyNoInteractions(userRepository);
        Mockito.verifyNoInteractions(userMapper);
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldReturnException_UserNotFound_PartiallyUpdateUser() {
        Long id = 1111L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.partialUpdateUser(id, userDTOPatchRequest));

        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoInteractions(userMapper);
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldAbleToDeleteUser() {
        Long id = 1L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.deleteUser(id);

        Mockito.verify(userRepository).findById(id);
        Mockito.verify(userRepository).deleteById(id);
    }

    @Test
    void shouldReturnError_NotAvailableId_DeleteUser() {
        Long id = 1L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(id));

        Mockito.verify(userRepository).findById(id);
        Mockito.verify(userRepository, Mockito.times(0)).deleteById(id);
    }
}