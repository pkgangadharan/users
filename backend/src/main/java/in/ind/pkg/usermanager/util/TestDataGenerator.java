package in.ind.pkg.usermanager.util;

import in.ind.pkg.usermanager.config.MultiTenantConfig;
import in.ind.pkg.usermanager.model.User;
import in.ind.pkg.usermanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TestDataGenerator implements CommandLineRunner {

    private final UserRepository userRepository;
    private final String[] tenants = {"tenant1", "tenant2"};

    public TestDataGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        for (String tenant : tenants) {
            MultiTenantConfig.setCurrentTenant(tenant);
            generateUsers(tenant, 10_000_000);
            MultiTenantConfig.clear();
        }
    }

    private void generateUsers(String tenantId, int count) {
        System.out.println("Generating users for tenant: " + tenantId);
        for (int i = 1; i <= count; i++) {
            User user = new User();
            user.setId(System.currentTimeMillis() + i); // TSID-like
            user.setFirstName("First" + i);
            user.setLastName("Last" + i);
            user.setEmail("user" + i + "_" + tenantId + "@example.com");
            user.setDateOfBirth(LocalDate.of(1990, 1, 1).plusDays(i % 10000));

            userRepository.save(user); // Consider batching for performance

            if (i % 100_000 == 0) {
                System.out.println("Inserted " + i + " users for " + tenantId);
            }
        }
        System.out.println("Completed generation for tenant: " + tenantId);
    }
}
