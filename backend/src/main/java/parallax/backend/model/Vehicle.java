package parallax.backend.model;

/**
 * Represents a vehicle registered to a user, including license details and blacklist status.
 */
public class Vehicle {
    private String username;
    private String licenseNumber;
    private String make;
    private String model;
    private String year;
    private boolean blacklisted;
    private String createdAt;

    public Vehicle() {
    }

    /**
     * Returns the username of the vehicle owner.
     *
     * @return owner username/email
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the vehicle owner.
     *
     * @param username owner username/email
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the license plate number as provided.
     *
     * @return license plate
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * Sets the license plate number.
     *
     * @param licenseNumber license plate
     */
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    /**
     * Returns the vehicle make/manufacturer.
     *
     * @return make
     */
    public String getMake() {
        return make;
    }

    /**
     * Sets the vehicle make/manufacturer.
     *
     * @param make make
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Returns the vehicle model.
     *
     * @return model
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the vehicle model.
     *
     * @param model model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the vehicle model year.
     *
     * @return year
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the vehicle model year.
     *
     * @param year year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Indicates whether the license plate is blacklisted.
     *
     * @return {@code true} when blacklisted
     */
    public boolean isBlacklisted() {
        return blacklisted;
    }

    /**
     * Sets the blacklist flag for the license plate.
     *
     * @param blacklisted blacklist flag
     */
    public void setBlacklisted(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    /**
     * Returns the timestamp when the vehicle was registered.
     *
     * @return creation timestamp
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the vehicle was registered.
     *
     * @param createdAt creation timestamp
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
