package parallax.backend.db;

import parallax.backend.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserRepository {
    // TODO: replace in-memory map with real SQLite queries using DataSource
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public UserRepository() {
        // TODO: replace in-memory map with real SQLite queries using DataSource
        User demo = new User("demo@parallax.test", "demo@parallax.test", "Demo User", "DemoPass123");
        users.put(demo.getUsername().toLowerCase(), demo);
    }

    public Optional<User> findByIdentifierAndPassword(String identifier, String password) {
        if (identifier == null || password == null) {
            return Optional.empty();
        }
        // TODO: replace in-memory map with real SQLite queries using DataSource
        String key = identifier.toLowerCase();
        User user = users.get(key);
        if (user != null && password.equals(user.getPassword())) {
            return Optional.of(user);
        }

        // Allow login by phone number as a secondary identifier in the demo
        String phoneDigits = identifier.replaceAll("\\D", "");
        Optional<User> byPhone = users.values().stream()
                .filter(u -> u.getPhone() != null && u.getPhoneCountry() != null)
                .filter(u -> (u.getPhoneCountry() + u.getPhone()).replaceAll("\\D", "").equals(phoneDigits))
                .findFirst();

        if (byPhone.isPresent() && password.equals(byPhone.get().getPassword())) {
            return byPhone;
        }

        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        // TODO: replace with SQLite query filtering by email
        return Optional.ofNullable(users.get(email.toLowerCase()));
    }

    public Optional<User> findByPhone(String phoneCountry, String phoneDigits) {
        if (phoneCountry == null || phoneDigits == null) {
            return Optional.empty();
        }
        // TODO: replace with SQLite query filtering by phone
        String signature = (phoneCountry + phoneDigits).replaceAll("\\D", "");
        return users.values().stream()
                .filter(u -> u.getPhoneCountry() != null && u.getPhone() != null)
                .filter(u -> (u.getPhoneCountry() + u.getPhone()).replaceAll("\\D", "").equals(signature))
                .findFirst();
    }

    public User createUser(User user) {
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("User and username must not be null");
        }
        // TODO: replace with INSERT statement against SQLite
        String key = user.getUsername().toLowerCase();
        users.put(key, user);
        return user;
    }

    public Map<String, User> findAllUsers() {
        // Helper primarily for testing/debugging
        return users.values().stream().collect(Collectors.toUnmodifiableMap(User::getUsername, u -> u));
    }
}
