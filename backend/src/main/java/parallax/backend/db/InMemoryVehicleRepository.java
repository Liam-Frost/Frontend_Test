package parallax.backend.db;

import parallax.backend.model.User;
import parallax.backend.model.Vehicle;
import parallax.backend.model.VehicleWithOwner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of {@link VehicleRepository} for local development and testing.
 * <p>
 * Vehicle data is stored per user in synchronized lists held in memory, so all registrations and
 * blacklist flags are cleared when the server restarts. A persistent SQLite-backed repository will
 * replace this class once available.
 * </p>
 */
public class InMemoryVehicleRepository implements VehicleRepository {
    // TODO: replace in-memory map with real SQLite queries using DataSource
    private final Map<String, List<Vehicle>> vehiclesByUser = new ConcurrentHashMap<>();

    private String normalizeLicense(String licenseNumber) {
        if (licenseNumber == null) {
            return null;
        }
        return licenseNumber.trim().toUpperCase();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Vehicles are moved between user buckets, and the username field on each vehicle is updated
     * to reflect the new owner.
     * </p>
     */
    @Override
    public List<Vehicle> reassignVehicles(String oldUsername, String newUsername) {
        if (oldUsername == null || newUsername == null) {
            return Collections.emptyList();
        }

        String oldKey = oldUsername.toLowerCase();
        String newKey = newUsername.toLowerCase();

        List<Vehicle> existing = vehiclesByUser.remove(oldKey);
        if (existing == null || existing.isEmpty()) {
            return Collections.emptyList();
        }

        existing.forEach(vehicle -> vehicle.setUsername(newUsername));
        vehiclesByUser.computeIfAbsent(newKey, k -> Collections.synchronizedList(new ArrayList<>()))
                .addAll(existing);
        return new ArrayList<>(existing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Vehicle> findByUsername(String username) {
        if (username == null) {
            return Collections.emptyList();
        }
        // TODO: replace with SELECT query filtered by username
        return new ArrayList<>(vehiclesByUser.getOrDefault(username.toLowerCase(), Collections.emptyList()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Vehicle> findByUsernameAndLicense(String username, String licenseNumber) {
        String normalizedLicense = normalizeLicense(licenseNumber);
        if (username == null || normalizedLicense == null) {
            return Optional.empty();
        }
        // TODO: replace with SELECT query filtered by username + license
        return vehiclesByUser.getOrDefault(username.toLowerCase(), Collections.emptyList())
                .stream()
                .filter(v -> normalizedLicense.equals(normalizeLicense(v.getLicenseNumber())))
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Vehicle> findByLicense(String licenseNumber) {
        return findByPlate(licenseNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Vehicle> findByPlate(String licenseNumber) {
        String normalizedLicense = normalizeLicense(licenseNumber);
        if (normalizedLicense == null) {
            return Optional.empty();
        }
        return vehiclesByUser.values().stream()
                .flatMap(List::stream)
                .filter(v -> normalizedLicense.equals(normalizeLicense(v.getLicenseNumber())))
                .findFirst();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Vehicles are appended to the owner's list without further validation; callers should perform
     * uniqueness checks before adding.
     * </p>
     */
    @Override
    public void addVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getUsername() == null) {
            throw new IllegalArgumentException("Vehicle and username must not be null");
        }
        // TODO: replace with INSERT against SQLite
        String key = vehicle.getUsername().toLowerCase();
        vehiclesByUser.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>())).add(vehicle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeVehicle(String username, String licenseNumber) {
        String normalizedLicense = normalizeLicense(licenseNumber);
        if (username == null || normalizedLicense == null) {
            return;
        }
        // TODO: replace with DELETE against SQLite
        List<Vehicle> list = vehiclesByUser.get(username.toLowerCase());
        if (list == null) {
            return;
        }
        list.removeIf(v -> normalizedLicense.equals(normalizeLicense(v.getLicenseNumber())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeVehiclesForUser(String username) {
        if (username == null) {
            return;
        }
        vehiclesByUser.remove(username.toLowerCase());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Iterates over all user buckets to remove any matching license plate.
     * </p>
     */
    @Override
    public boolean removeByLicense(String licenseNumber) {
        String normalizedLicense = normalizeLicense(licenseNumber);
        if (normalizedLicense == null) {
            return false;
        }
        boolean removed = false;
        for (List<Vehicle> vehicles : vehiclesByUser.values()) {
            removed |= vehicles.removeIf(v -> normalizedLicense.equals(normalizeLicense(v.getLicenseNumber())));
        }
        return removed;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Blacklist status is stored directly on the vehicle record within the in-memory map.
     * </p>
     */
    @Override
    public Optional<Vehicle> updateBlacklistStatus(String licenseNumber, boolean blacklisted) {
        Optional<Vehicle> match = findByLicense(licenseNumber);
        match.ifPresent(vehicle -> vehicle.setBlacklisted(blacklisted));
        return match;
    }

    /**
     * Returns a flat copy of all vehicles held in memory.
     *
     * @return list of every vehicle across all users
     */
    public List<Vehicle> findAll() {
        return vehiclesByUser.values().stream()
                .flatMap(List::stream)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VehicleWithOwner> findAllWithOwners(UserRepository userRepository) {
        List<VehicleWithOwner> results = new ArrayList<>();
        for (Vehicle vehicle : findAll()) {
            VehicleWithOwner enriched = new VehicleWithOwner();
            enriched.setUsername(vehicle.getUsername());
            enriched.setLicenseNumber(vehicle.getLicenseNumber());
            enriched.setMake(vehicle.getMake());
            enriched.setModel(vehicle.getModel());
            enriched.setYear(vehicle.getYear());
            enriched.setBlacklisted(vehicle.isBlacklisted());
            enriched.setCreatedAt(vehicle.getCreatedAt());

            String ownerKey = vehicle.getUsername();
            if (ownerKey != null) {
                userRepository.findByEmail(ownerKey).ifPresent((User user) -> {
                    enriched.setOwnerUsername(user.getUsername());
                    enriched.setOwnerEmail(user.getEmail());
                    enriched.setOwnerPhone(user.getPhoneCountry() != null
                            ? user.getPhoneCountry() + (user.getPhone() == null ? "" : user.getPhone())
                            : user.getPhone());
                    enriched.setOwnerPhoneCountry(user.getPhoneCountry());
                });
            }
            results.add(enriched);
        }
        return results;
    }
}
