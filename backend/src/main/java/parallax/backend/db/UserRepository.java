package parallax.backend.db;

import parallax.backend.model.User;

import java.util.Map;
import java.util.Optional;

/**
 * Abstraction for user persistence operations.
 * <p>
 * Implementations provide lookup, creation, update, and deletion of user records while hiding
 * the underlying storage mechanism (currently in-memory, later SQLite).
 * </p>
 */
public interface UserRepository {
    /**
     * Attempts to authenticate a user by identifier and password.
     * The identifier may be an email address or phone number depending on implementation.
     *
     * @param identifier user-provided login identifier
     * @param password   plaintext password to verify
     * @return matching user when credentials are valid; otherwise {@link Optional#empty()}
     */
    Optional<User> findByIdentifierAndPassword(String identifier, String password);

    /**
     * Looks up a user by unique email/username value.
     *
     * @param email normalized email address
     * @return user if present; otherwise {@link Optional#empty()}
     */
    Optional<User> findByEmail(String email);

    /**
     * Looks up a user by normalized phone components.
     *
     * @param phoneCountry country/region dialing prefix
     * @param phoneDigits  phone number digits without formatting characters
     * @return user if the phone signature matches; otherwise {@link Optional#empty()}
     */
    Optional<User> findByPhone(String phoneCountry, String phoneDigits);

    /**
     * Persists a new user record.
     * Implementations may choose to upsert or enforce uniqueness based on the username/email.
     *
     * @param user user to store
     * @return persisted user instance
     */
    User createUser(User user);

    /**
     * Updates contact information for an existing user, potentially renaming the username/email key.
     *
     * @param username    current unique username/email
     * @param newEmail    replacement email/username
     * @param phoneCountry updated country code, may be {@code null}
     * @param phone       updated phone digits, may be {@code null}
     * @return updated user if present; otherwise {@link Optional#empty()}
     */
    Optional<User> updateContact(String username, String newEmail, String phoneCountry, String phone);

    /**
     * Replaces the stored password for the specified user.
     *
     * @param username    user identifier whose password should be changed
     * @param newPassword new plaintext password value
     * @return updated user when found; otherwise {@link Optional#empty()}
     */
    Optional<User> updatePassword(String username, String newPassword);

    /**
     * Deletes the user identified by the provided username.
     *
     * @param username unique username/email key
     * @return {@code true} when a user was removed; {@code false} if no matching record existed
     */
    boolean deleteUser(String username);

    /**
     * Returns a snapshot of all users keyed by username.
     * Primarily intended for administrative dashboards or validation utilities.
     *
     * @return immutable map of username to {@link User}
     */
    Map<String, User> findAllUsers();
}
