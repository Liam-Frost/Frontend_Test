package parallax.backend.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parallax.backend.model.Vehicle;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryVehicleRepositoryTest {
    private InMemoryVehicleRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryVehicleRepository();
    }

    @Test
    void addVehicle_storesAndFindsByUsernameAndLicense() {
        Vehicle vehicle = new Vehicle();
        vehicle.setUsername("owner@example.com");
        vehicle.setLicenseNumber("abc123");
        vehicle.setMake("Tesla");
        vehicle.setModel("Model 3");
        vehicle.setYear("2023");

        repository.addVehicle(vehicle);

        Optional<Vehicle> found = repository.findByUsernameAndLicense("owner@example.com", "ABC123");
        assertTrue(found.isPresent());
        assertEquals("ABC123", found.get().getLicenseNumber());
    }

    @Test
    void findByUsername_returnsOnlyOwnersVehicles() {
        Vehicle first = new Vehicle();
        first.setUsername("user@example.com");
        first.setLicenseNumber("AAA111");
        repository.addVehicle(first);

        Vehicle second = new Vehicle();
        second.setUsername("other@example.com");
        second.setLicenseNumber("BBB222");
        repository.addVehicle(second);

        List<Vehicle> userVehicles = repository.findByUsername("user@example.com");
        assertEquals(1, userVehicles.size());
        assertEquals("AAA111", userVehicles.get(0).getLicenseNumber());
    }

    @Test
    void removeVehicle_deletesMatchingEntry() {
        Vehicle vehicle = new Vehicle();
        vehicle.setUsername("owner@example.com");
        vehicle.setLicenseNumber("abc123");
        repository.addVehicle(vehicle);

        repository.removeVehicle("owner@example.com", "ABC123");

        assertTrue(repository.findByUsernameAndLicense("owner@example.com", "ABC123").isEmpty());
    }

    @Test
    void updateBlacklistStatus_togglesFlag() {
        Vehicle vehicle = new Vehicle();
        vehicle.setUsername("owner@example.com");
        vehicle.setLicenseNumber("abc123");
        vehicle.setBlacklisted(false);
        repository.addVehicle(vehicle);

        repository.updateBlacklistStatus("ABC123", true);

        Optional<Vehicle> found = repository.findByLicense("abc123");
        assertTrue(found.isPresent());
        assertTrue(found.get().isBlacklisted());
    }
}
