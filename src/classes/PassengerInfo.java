package classes;

public class PassengerInfo {
    private final String fullName;
    private final Integer age; // int vs Integer difference?

    public PassengerInfo(String fullName, Integer age) { // Parameter vs argument difference?

        if (fullName == null || fullName.isBlank()){
            throw new IllegalArgumentException("Passenger name cannot be empty.");
        }

        if (age == null || age <= 0){
            throw new IllegalArgumentException("Passenger age must be greater than 0.");
        }

        this.fullName = fullName;
        this.age = age;
    }

    public String getFullName() {
        return fullName;
    }

    public Integer getAge() {
        return age;
    }

    @Override
    public String toString() {
        return fullName + " (" + age + ")";
    }
}