package parallax.backend.model;

public class VehicleView {
    private String licenseNumber;
    private String make;
    private String model;
    private String year;
    private boolean blacklisted;
    private String createdAt;
    private String ownerEmail;
    private String ownerPhoneCountry;
    private String ownerPhone;

    public static VehicleView from(Vehicle vehicle, User owner) {
        VehicleView view = new VehicleView();
        if (vehicle != null) {
            view.setLicenseNumber(vehicle.getLicenseNumber());
            view.setMake(vehicle.getMake());
            view.setModel(vehicle.getModel());
            view.setYear(vehicle.getYear());
            view.setBlacklisted(vehicle.isBlacklisted());
            view.setCreatedAt(vehicle.getCreatedAt());
        }
        if (owner != null) {
            view.setOwnerEmail(owner.getEmail());
            view.setOwnerPhoneCountry(owner.getPhoneCountry());
            view.setOwnerPhone(owner.getPhone());
        }
        return view;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }

    public void setBlacklisted(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhoneCountry() {
        return ownerPhoneCountry;
    }

    public void setOwnerPhoneCountry(String ownerPhoneCountry) {
        this.ownerPhoneCountry = ownerPhoneCountry;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }
}
