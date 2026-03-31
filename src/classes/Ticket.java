package classes;

import enums.TripType;

public class Ticket {
    private final Trip trip; // Which trip?
    private final Integer seatNumber; // Which seat?
    private final PassengerInfo passenger; // Which passenger?
    private Double initialPrice; // Initial price
    private final Double finalPrice; // Final total amount
    // Bus can be added to apply additional price if the bus has single seats.

    public Ticket(Trip trip, Integer seatNumber, PassengerInfo passenger) {
        this.trip = trip;
        this.seatNumber = seatNumber;
        this.passenger = passenger;
        this.finalPrice = calculatePrice();
    }

    private Double calculatePrice() {
        Double distance = trip.getDistance();
        TripType tripType = trip.getTripType();

        // Base price per kilometer
        // One way 1.5 TL
        // Round trip 2.5 TL
        Double pricePerKm = (tripType == TripType.ONE_WAY ? 1.5 : 2.5);

        this.initialPrice = distance * pricePerKm;
        double price = this.initialPrice;

        // Discounts
        // 1- Age discount
        int age = passenger.getAge();

        if (age <= 12) {
            price = price * 0.5;
        }else if (age <= 25) {
            price = price * 0.7;
        } else if (age >= 65){
            price = price * 0.75;
        }

        // 2- Round trip discount
        if (tripType.equals(TripType.ROUND_TRIP)){
            price *= 0.8;
        }

        return price;
    }

    public Trip getTrip() {
        return trip;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public PassengerInfo getPassenger() {
        return passenger;
    }

    public Double getInitialPrice() {
        return initialPrice;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }
}