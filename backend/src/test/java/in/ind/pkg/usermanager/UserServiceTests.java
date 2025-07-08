package in.ind.pkg.usermanager;

import in.ind.pkg.usermanager.model.User;
import in.ind.pkg.usermanager.repository.UserRepository;
import in.ind.pkg.usermanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTests {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService();
        userService = Mockito.spy(userService);
        Mockito.doReturn(System.currentTimeMillis()).when(userService).generateTsid(); // optional override
        userService = new UserService();
        userService = Mockito.spy(userService);
        Mockito.doCallRealMethod().when(userService).addUser(any());
        Mockito.doCallRealMethod().when(userService).updateUser(any(), any());
        Mockito.doCallRealMethod().when(userService).deleteUser(any());
        Mockito.doCallRealMethod().when(userService).getUser(any());
        Mockito.doCallRealMethod().when(userService).getAllUsers();
        Mockito.doCallRealMethod().when(userService).getUsers(any());
        Mockito.doCallRealMethod().when(userService).searchUsers(any(), any());
        Mockito.doReturn(userRepository).when(userService).getUserRepository();
    }

    @Test
    void testAddUser_Success() {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User saved = i.getArgument(0);
            saved.setId(123456789L);
            return saved;
        });

        User result = userService.addUser(user);
        assertNotNull(result.getId());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testAddUser_DuplicateEmail_ThrowsException() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.addUser(user));
    }

    @Test
    void testUpdateUser_Success() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("old@example.com");
        existing.setFirstName("Old");
        existing.setLastName("Name");

        User updated = new User();
        updated.setEmail("new@example.com");
        updated.setFirstName("New");
        updated.setLastName("Name");
        updated.setDateOfBirth(LocalDate.of(1991, 1, 1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(1L, updated);
        assertEquals("new@example.com", result.getEmail());
        assertEquals("New", result.getFirstName());
    }

    @Test
    void testDeleteUser_NotFound_ThrowsException() {
        when(userRepository.existsById(99L)).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> userService.deleteUser(99L));
    }
}
