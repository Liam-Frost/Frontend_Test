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

public class VehicleRepository {
    // TODO: replace in-memory map with real SQLite queries using DataSource
    private final Map<String, List<Vehicle>> vehiclesByUser = new ConcurrentHashMap<>();

    public List<Vehicle> findByUsername(String username) {
        if (username == null) {
            return Collections.emptyList();
        }
        // TODO: replace with SELECT query filtered by username
        return new ArrayList<>(vehiclesByUser.getOrDefault(username.toLowerCase(), Collections.emptyList()));
    }

    public Optional<Vehicle> findByUsernameAndLicense(String username, String licenseNumber) {
        if (username == null || licenseNumber == null) {
            return Optional.empty();
        }
        // TODO: replace with SELECT query filtered by username + license
        return vehiclesByUser.getOrDefault(username.toLowerCase(), Collections.emptyList())
                .stream()
                .filter(v -> licenseNumber.equalsIgnoreCase(v.getLicenseNumber()))
                .findFirst();
    }

    public Optional<Vehicle> findByLicense(String licenseNumber) {
        if (licenseNumber == null) {
            return Optional.empty();
        }
        return vehiclesByUser.values().stream()
                .flatMap(List::stream)
                .filter(v -> licenseNumber.equalsIgnoreCase(v.getLicenseNumber()))
                .findFirst();
    }

    public void addVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getUsername() == null) {
            throw new IllegalArgumentException("Vehicle and username must not be null");
        }
        // TODO: replace with INSERT against SQLite
        String key = vehicle.getUsername().toLowerCase();
        vehiclesByUser.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>())).add(vehicle);
    }

    public void removeVehicle(String username, String licenseNumber) {
        if (username == null || licenseNumber == null) {
            return;
        }
        // TODO: replace with DELETE against SQLite
        List<Vehicle> list = vehiclesByUser.get(username.toLowerCase());
        if (list == null) {
            return;
        }
        list.removeIf(v -> licenseNumber.equalsIgnoreCase(v.getLicenseNumber()));
    }

    public boolean removeByLicense(String licenseNumber) {
        if (licenseNumber == null) {
            return false;
        }
        boolean removed = false;
        for (List<Vehicle> vehicles : vehiclesByUser.values()) {
            removed |= vehicles.removeIf(v -> licenseNumber.equalsIgnoreCase(v.getLicenseNumber()));
        }
        return removed;
    }

    public Optional<Vehicle> updateBlacklistStatus(String licenseNumber, boolean blacklisted) {
        Optional<Vehicle> match = findByLicense(licenseNumber);
        match.ifPresent(vehicle -> vehicle.setBlacklisted(blacklisted));
        return match;
    }

    public List<Vehicle> findAll() {
        return vehiclesByUser.values().stream()
                .flatMap(List::stream)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

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
