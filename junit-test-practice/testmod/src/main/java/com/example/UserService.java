package com.example;

// a small service with a dependancy we'll mock in tests.
import java.util.Objects;

// relies on UserRepository - handles user registration.
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = Objects.requireNonNull(repo, "UserRepository cannot be null");

    }



    // Registers a new user. check if email is valid email is already registered
    public User register(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (repo.existsByEmail(email))
            throw new IllegalStateException("Email already registered");

        User user = new User(email);
        repo.save(user);
        return user;
    }



    public static class User{
        private final String email;
        public User(String email) {
            this.email = email;
        }
        public String getEmail() {
            return email;
        }
    }
    // this is not to be implemented, just mocked to provide a controlled implementation.
    public interface UserRepository {
        boolean existsByEmail(String email);
        void save(UserService.User user);
}

}
