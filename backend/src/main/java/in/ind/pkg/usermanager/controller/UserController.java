package in.ind.pkg.usermanager.controller;

import in.ind.pkg.usermanager.model.User;
import in.ind.pkg.usermanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Add user
    @PostMapping
    public User addUser(@RequestBody @Valid User user, @RequestHeader("x-tenant-id") String tenantId) {
        return userService.addUser(user);
    }

    // Update user
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody @Valid User user,
                           @RequestHeader("x-tenant-id") String tenantId) {
        return userService.updateUser(id, user);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id, @RequestHeader("x-tenant-id") String tenantId) {
        userService.deleteUser(id);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id, @RequestHeader("x-tenant-id") String tenantId) {
        return userService.getUser(id);
    }

    // Get all users (paginated)
    @GetMapping
    public Page<User> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("x-tenant-id") String tenantId) {
        return userService.getUsers(PageRequest.of(page, size));
    }
}
