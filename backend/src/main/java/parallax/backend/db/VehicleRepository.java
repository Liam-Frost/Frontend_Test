package parallax.backend.db;

import parallax.backend.model.Vehicle;
import parallax.backend.model.VehicleWithOwner;

import java.util.List;
import java.util.Optional;

/**
 * Abstraction for persistence of vehicles and license plate state.
 * Implementations should enforce per-user license uniqueness and provide blacklist management.
 */
public interface VehicleRepository {
    /**
     * Transfers all vehicles from one user to another, updating the stored owner reference.
     *
     * @param oldUsername current owner username/email
     * @param newUsername replacement owner username/email
     * @return list of vehicles that were reassigned (empty if none)
     */
    List<Vehicle> reassignVehicles(String oldUsername, String newUsername);

    /**
     * Retrieves all vehicles registered to the specified user.
     *
     * @param username owner username/email
     * @return mutable copy of the user's vehicles
     */
    List<Vehicle> findByUsername(String username);

    /**
     * Looks up a vehicle by user and license plate combination.
     *
     * @param username      expected owner username/email
     * @param licenseNumber license plate text (case-insensitive)
     * @return vehicle if a matching record exists; otherwise {@link Optional#empty()}
     */
    Optional<Vehicle> findByUsernameAndLicense(String username, String licenseNumber);

    /**
     * Finds a vehicle across all users using the raw license number.
     *
     * @param licenseNumber license plate text (case-insensitive)
     * @return matching vehicle when present
     */
    Optional<Vehicle> findByLicense(String licenseNumber);

    /**
     * Alias for {@link #findByLicense(String)} for callers using normalized plate terminology.
     *
     * @param licenseNumber license plate text (case-insensitive)
     * @return matching vehicle when present
     */
    Optional<Vehicle> findByPlate(String licenseNumber);

    /**
     * Persists a vehicle for a user. Implementations may enforce uniqueness per owner.
     *
     * @param vehicle vehicle to add
     */
    void addVehicle(Vehicle vehicle);

    /**
     * Removes a vehicle tied to the provided user and license plate.
     *
     * @param username      owner username/email
     * @param licenseNumber license plate text (case-insensitive)
     */
    void removeVehicle(String username, String licenseNumber);

    /**
     * Removes all vehicles belonging to the supplied user.
     *
     * @param username owner username/email
     */
    void removeVehiclesForUser(String username);

    /**
     * Removes any vehicle matching the provided license, regardless of owner.
     *
     * @param licenseNumber license plate text (case-insensitive)
     * @return {@code true} when at least one record was removed
     */
    boolean removeByLicense(String licenseNumber);

    /**
     * Toggles the blacklist flag on the specified license plate.
     *
     * @param licenseNumber license plate to update
     * @param blacklisted   desired blacklist status
     * @return updated vehicle if present; otherwise {@link Optional#empty()}
     */
    Optional<Vehicle> updateBlacklistStatus(String licenseNumber, boolean blacklisted);

    /**
     * Retrieves all vehicles enriched with owner contact details for administrative views.
     *
     * @param userRepository repository used to resolve owner contact data
     * @return list of vehicles including owner metadata
     */
    List<VehicleWithOwner> findAllWithOwners(UserRepository userRepository);
}
