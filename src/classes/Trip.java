package classes;

import enums.SeatFormat;
import enums.SeatPosition;
import enums.TripType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trip {
    private final String from; // From where?
    private final String to; // To where?
    private final String departureAt; // Departure date
    private final Double distance; // How many kilometers?
    private final TripType tripType; // Round trip or one way?
    private final Bus bus;

    private Map<Integer, PassengerInfo> reservations = new HashMap<>();

    public Trip(String from, String to, String departureAt, Double distance, TripType tripType, Bus bus) {

        if (distance == null || distance <= 0){
            throw new IllegalArgumentException("Distance must be greater than 0 kilometers.");
        }

        this.from = from;
        this.to = to;
        this.departureAt = departureAt;
        this.distance = distance;
        this.tripType = tripType;
        this.bus = bus;
    }

    // Helper 1
    private boolean isSeatTaken(int seatNumber) {
        return this.reservations.containsKey(seatNumber);
    }

    public Ticket reserveSeatAndIssueTicket(int seatNumber, String fullName, int age){
        // 1- Find the seat
        boolean seatExists = false;

        for (Seat s : bus.getSeats()){
            if (s.getSeatNumber() == seatNumber){
                seatExists = true;
                break;
            }
        }

        if (!seatExists){
            System.out.println("Seat number " + seatNumber + " does not exist!");
            return null;
        }

        // 2- Is the seat taken?
        if (isSeatTaken(seatNumber)){
            System.out.println("Seat number " + seatNumber + " is already taken!");
            return null;
        }

        // 3- Create passenger information
        PassengerInfo passenger = new PassengerInfo(fullName, age);
        reservations.put(seatNumber, passenger);

        return new Ticket(this, seatNumber, passenger);
    }

    public void printSeatAvailability(){
        System.out.println("Trip route: " + from + " -> " + to + " - " + departureAt);
        System.out.println("Distance: " + distance + "km | Trip type: " + tripType);
        System.out.println("\n======== Trip Seat Layout ========");

        List<Seat> seats = bus.getSeats();

        int maxRowIndex = seats.stream()
                .map(Seat::getRowIndex)
                .filter(rowIndex -> rowIndex >=0)
                .max(Integer::compareTo)
                .orElse(-1);

        SeatFormat seatFormat = bus.getSeatFormat();

        // Body seats
        for (int row = 0; row <= maxRowIndex; row++){
            if (seatFormat == SeatFormat.TWO_PLUS_ONE){
                Seat left = findSeat(seats, row, SeatPosition.LEFT);
                Seat rightInner = findSeat(seats, row, SeatPosition.RIGHT_INNER);
                Seat right = findSeat(seats, row, SeatPosition.RIGHT);

                // Print the seats
                printSeatCell(left);
                System.out.print("      ");
                printSeatCell(rightInner);
                printSeatCell(right);

                System.out.println();
            } else { // 2+2
                Seat left = findSeat(seats, row, SeatPosition.LEFT);
                Seat leftInner = findSeat(seats, row, SeatPosition.LEFT_INNER);
                Seat rightInner = findSeat(seats, row, SeatPosition.RIGHT_INNER);
                Seat right = findSeat(seats, row, SeatPosition.RIGHT);

                // Print the seats
                printSeatCell(left);
                printSeatCell(leftInner);
                System.out.print("    ");
                printSeatCell(rightInner);
                printSeatCell(right);

                System.out.println();
            }
        }


        // Back row seats
        List<Seat> backRowSeats = new ArrayList<>();
        for (Seat s : seats) {
            if (s.getRowIndex() == -1) {
                backRowSeats.add(s);
            }
        }

        if (!backRowSeats.isEmpty()){
            for (Seat s : backRowSeats){
                printSeatCell(s);
            }
            System.out.println();
        }

        System.out.println("====================================");
    }

    // Helper 2
    private Seat findSeat(List<Seat> seats, int rowIndex, SeatPosition position){
        for (Seat s : seats){
            if (s.getRowIndex() == rowIndex && s.getSeatPosition() == position){
                return s;
            }
        }
        return null; // No seat found at the specified row and position!!!
    }

    // Helper 3
    private void printSeatCell(Seat seat){
        if (seat == null){
            System.out.print("      ");
            return;
        }

        int number = seat.getSeatNumber();
        boolean taken = isSeatTaken(number);

        char status = taken ? 'x' : 'o';
        System.out.printf("[%2d %c]", number, status);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDepartureAt() {
        return departureAt;
    }

    public Double getDistance() {
        return distance;
    }

    public TripType getTripType() {
        return tripType;
    }

    public Bus getBus() {
        return bus;
    }
}