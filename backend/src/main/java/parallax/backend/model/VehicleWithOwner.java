package parallax.backend.model;

/**
 * DTO combining vehicle data with owner contact information for administrative views.
 */
public class VehicleWithOwner extends Vehicle {
    private String ownerUsername;
    private String ownerEmail;
    private String ownerPhone;
    private String ownerPhoneCountry;

    /**
     * Returns the owner's username/email.
     *
     * @return owner username
     */
    public String getOwnerUsername() {
        return ownerUsername;
    }

    /**
     * Sets the owner's username/email.
     *
     * @param ownerUsername owner username
     */
    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    /**
     * Returns the owner's email address.
     *
     * @return owner email
     */
    public String getOwnerEmail() {
        return ownerEmail;
    }

    /**
     * Sets the owner's email address.
     *
     * @param ownerEmail owner email
     */
    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    /**
     * Returns the owner's phone number (including country code when available).
     *
     * @return owner phone
     */
    public String getOwnerPhone() {
        return ownerPhone;
    }

    /**
     * Sets the owner's phone number (including country code when available).
     *
     * @param ownerPhone owner phone
     */
    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    /**
     * Returns the owner's phone country code.
     *
     * @return phone country code
     */
    public String getOwnerPhoneCountry() {
        return ownerPhoneCountry;
    }

    /**
     * Sets the owner's phone country code.
     *
     * @param ownerPhoneCountry phone country code
     */
    public void setOwnerPhoneCountry(String ownerPhoneCountry) {
        this.ownerPhoneCountry = ownerPhoneCountry;
    }
}
