package parallax.backend.db;

import parallax.backend.model.Vehicle;

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
}
