package parallax.backend.db;

import parallax.backend.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link UserRepository} for demos and tests.
 * <p>
 * User data is held in a concurrent map and therefore reset on every server restart. The class
 * also seeds a demo account for quick manual testing. This implementation will be replaced by a
 * SQLite-backed repository when persistence is introduced.
 * </p>
 */
public class InMemoryUserRepository implements UserRepository {
    // TODO: replace in-memory map with real SQLite queries using DataSource
    private final Map<String, User> users = new ConcurrentHashMap<>();

    /**
     * Creates the repository with a pre-seeded demo user to simplify local testing.
     */
    public InMemoryUserRepository() {
        // TODO: replace in-memory map with real SQLite queries using DataSource
        User demo = new User("demo@parallax.test", "demo@parallax.test", "Demo User", "DemoPass123");
        users.put(demo.getUsername().toLowerCase(), demo);
    }

    @Override
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

    /**
     * {@inheritDoc}
     * <p>
     * The lookup is case-insensitive and limited to the in-memory map.
     * </p>
     */
    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        // TODO: replace with SQLite query filtering by email
        return Optional.ofNullable(users.get(email.toLowerCase()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Phone signatures are normalized by stripping non-digits before comparison.
     * </p>
     */
    @Override
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

    /**
     * {@inheritDoc}
     * <p>
     * This implementation performs an insert-or-replace into the in-memory map using a
     * lowercased username as the key.
     * </p>
     */
    @Override
    public User createUser(User user) {
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("User and username must not be null");
        }
        // TODO: replace with INSERT statement against SQLite
        String key = user.getUsername().toLowerCase();
        users.put(key, user);
        return user;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updating contact details may change the map key; when the username changes the associated
     * entry is reinserted under the new normalized email.
     * </p>
     */
    @Override
    public Optional<User> updateContact(String username, String newEmail, String phoneCountry, String phone) {
        if (username == null || newEmail == null) {
            return Optional.empty();
        }

        String key = username.toLowerCase();
        User existing = users.get(key);
        if (existing == null) {
            return Optional.empty();
        }

        String newKey = newEmail.toLowerCase();
        existing.setUsername(newKey);
        existing.setEmail(newKey);
        existing.setPhoneCountry(phoneCountry);
        existing.setPhone(phone);

        if (!newKey.equals(key)) {
            users.remove(key);
            users.put(newKey, existing);
        }

        return Optional.of(existing);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Passwords are stored as provided; no hashing is performed in the in-memory demo.
     * </p>
     */
    @Override
    public Optional<User> updatePassword(String username, String newPassword) {
        if (username == null || newPassword == null) {
            return Optional.empty();
        }
        User existing = users.get(username.toLowerCase());
        if (existing == null) {
            return Optional.empty();
        }
        existing.setPassword(newPassword);
        return Optional.of(existing);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Deletion removes the entry from the in-memory map and therefore cannot be recovered.
     * </p>
     */
    @Override
    public boolean deleteUser(String username) {
        if (username == null) {
            return false;
        }
        return users.remove(username.toLowerCase()) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, User> findAllUsers() {
        // Helper primarily for testing/debugging
        return users.values().stream().collect(Collectors.toUnmodifiableMap(User::getUsername, u -> u));
    }
}
