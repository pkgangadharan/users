package in.ind.pkg.usermanager.spec;

import in.ind.pkg.usermanager.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> emailContains(String keyword) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("email")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<User> firstNameContains(String keyword) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("firstName")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<User> lastNameContains(String keyword) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("lastName")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<User> emailEquals(String email) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("email")), email.toLowerCase());
    }
}
