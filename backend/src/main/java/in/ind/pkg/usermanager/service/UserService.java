package in.ind.pkg.usermanager.service;

import in.ind.pkg.usermanager.model.User;
import in.ind.pkg.usermanager.repository.UserRepository;
import in.ind.pkg.usermanager.spec.UserSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Generate a simple TSID-like ID using current epoch millis + random component
    public long generateTsid() {
        return Instant.now().toEpochMilli() << 10 | (long) (Math.random() * 1024);
    }

    public User addUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        user.setId(generateTsid());
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updated) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));

        if (!existing.getEmail().equalsIgnoreCase(updated.getEmail())
                && userRepository.existsByEmail(updated.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + updated.getEmail());
        }

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setDateOfBirth(updated.getDateOfBirth());

        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> searchUsers(String query, Pageable pageable) {
        Specification<User> spec = Specification.where(null);

        if (query != null && !query.isEmpty()) {
            spec = spec.or(UserSpecification.firstNameContains(query))
                       .or(UserSpecification.lastNameContains(query))
                       .or(UserSpecification.emailContains(query));
        }

        return userRepository.findAll(spec, pageable);
    }

    // Add this getter for testing/mocking purposes
    public UserRepository getUserRepository() {
        return userRepository;
    }
}
