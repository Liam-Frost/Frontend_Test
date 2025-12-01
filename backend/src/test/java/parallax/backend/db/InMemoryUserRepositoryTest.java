package parallax.backend.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parallax.backend.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {
    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    @Test
    void createUser_persistsAndRetrievesByEmail() {
        User user = new User();
        user.setUsername("User@Example.com");
        user.setEmail("User@Example.com");
        user.setPassword("Password123");

        repository.createUser(user);

        Optional<User> found = repository.findByEmail("user@example.com");
        assertTrue(found.isPresent());
        assertEquals("user@example.com", found.get().getUsername());
        assertEquals("Password123", found.get().getPassword());
    }

    @Test
    void findByIdentifierAndPassword_supportsPhoneLookup() {
        User user = new User();
        user.setUsername("phone@example.com");
        user.setEmail("phone@example.com");
        user.setPassword("PhonePass1");
        user.setPhoneCountry("+1");
        user.setPhone("5551234");
        repository.createUser(user);

        Optional<User> found = repository.findByIdentifierAndPassword("+1 (555) 1234", "PhonePass1");
        assertTrue(found.isPresent());
        assertEquals("phone@example.com", found.get().getUsername());
    }

    @Test
    void updateContact_rekeysUserAndPreservesData() {
        User user = new User();
        user.setUsername("old@example.com");
        user.setEmail("old@example.com");
        user.setPassword("Secret123");
        repository.createUser(user);

        repository.updateContact("old@example.com", "new@example.com", "+1", "9990000");

        assertTrue(repository.findByEmail("old@example.com").isEmpty());
        Optional<User> updated = repository.findByEmail("new@example.com");
        assertTrue(updated.isPresent());
        assertEquals("new@example.com", updated.get().getUsername());
        assertEquals("+1", updated.get().getPhoneCountry());
        assertEquals("9990000", updated.get().getPhone());
    }

    @Test
    void deleteUser_removesEntry() {
        User user = new User();
        user.setUsername("delete@example.com");
        user.setEmail("delete@example.com");
        repository.createUser(user);

        assertTrue(repository.deleteUser("delete@example.com"));
        assertTrue(repository.findByEmail("delete@example.com").isEmpty());
    }
}
