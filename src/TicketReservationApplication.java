import classes.Bus;
import classes.Ticket;
import classes.Trip;
import enums.NumberingStyle;
import enums.SeatFormat;
import enums.TripType;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TicketReservationApplication {
    public static void main(String[] args) {
        Bus bus1 = new Bus(SeatFormat.TWO_PLUS_ONE, 41, true, NumberingStyle.CONTINUOUS);

        Bus bus2 = new Bus(SeatFormat.TWO_PLUS_TWO, 55, true, NumberingStyle.CONTINUOUS);

        Bus bus3 = new Bus(SeatFormat.TWO_PLUS_ONE, 41, true, NumberingStyle.SKIPPING);

        List<Trip> trips = new ArrayList<>();

        trips.add(new Trip("Ankara", "Istanbul", "10/11/2025 - 16:00", 450.0, TripType.ONE_WAY, bus1));
        trips.add(new Trip("Ankara", "Izmir", "10/11/2025 - 23:30", 670.0, TripType.ONE_WAY, bus2));
        trips.add(new Trip("Izmir", "Ankara", "11/11/2025 - 10:00", 670.0, TripType.ONE_WAY, bus2));
        trips.add(new Trip("Ankara", "Istanbul", "10/11/2025 - 14:00", 900.0, TripType.ROUND_TRIP, bus3));
        trips.add(new Trip("Istanbul", "Ankara", "10/11/2025 - 23:30", 450.0, TripType.ONE_WAY, bus1));
        trips.add(new Trip("Ankara", "Istanbul", "11/11/2025 - 07:30", 450.0, TripType.ONE_WAY, bus1));

        start(trips);
    }

    private static void start(List<Trip> trips) {
        Scanner input = new Scanner(System.in);
        System.out.println("==== Welcome to the Ticket Reservation System ====");

        do {
            System.out.println("From (city):");
            System.err.println("Type -1 to exit.");
            String fromCity = input.nextLine().trim();

            if (fromCity.equals("-1")){
                break;
            }

            System.out.println("To (city):");
            String toCity = input.nextLine().trim();

            System.out.println("Date (DD/MM/YYYY):");
            String date = input.nextLine().trim();

            // Trip filtering
            List<Trip> matchingTrips = new ArrayList<>();
            for (Trip trip : trips) {
                boolean fromMatch = trip.getFrom().equalsIgnoreCase(fromCity);
                boolean toMatch = trip.getTo().equalsIgnoreCase(toCity);
                boolean dateMatch = trip.getDepartureAt().startsWith(date);

                if (fromMatch && toMatch && dateMatch) {
                    matchingTrips.add(trip);
                }
            }

            // If no trips found, go back to the beginning
            if (matchingTrips.isEmpty()){
                System.out.println("Sorry, no trips found matching your criteria.");
                System.out.println("Redirecting you to trip selection.");
                continue;
            }

            // Display available trips
            int tripChoice;
            while(true){
                System.out.println("Available trips:");
                for (int i = 0; i < matchingTrips.size(); i++){
                    Trip trip = matchingTrips.get(i);

                    String seatLayout = trip.getBus().getSeatFormat() == SeatFormat.TWO_PLUS_ONE
                            ? "2+1"
                            : "2+2";

                    System.out.printf("%d. %s -> %s | Date: %s | Distance: %.0fkm | Type: %s | Seat Layout: %s\n",
                            i+1,
                            trip.getFrom(),
                            trip.getTo(),
                            trip.getDepartureAt(),
                            trip.getDistance(),
                            trip.getTripType(),
                            seatLayout);
                }

                System.out.println("Select the trip you want. Enter the number.");
                tripChoice = input.nextInt();
                input.nextLine();

                if (tripChoice < 1 || tripChoice > matchingTrips.size()){
                    System.err.println("Invalid selection. Please try again.");
                    continue;
                }
                break;
            }

            // Get the selected trip
            Trip selectedTrip = matchingTrips.get(tripChoice - 1);

            System.out.printf("\nSelected trip: %s -> %s | %s | %.0fkm | %s\n",
                    selectedTrip.getFrom(),
                    selectedTrip.getTo(),
                    selectedTrip.getDepartureAt(),
                    selectedTrip.getDistance(),
                    selectedTrip.getTripType()
            );

            selectedTrip.printSeatAvailability();

            // Seat reservation loop
            while (true){
                System.out.println("\nEnter passenger name. Type -1 to exit.");
                String fullName = input.nextLine();
                if (fullName.equals("-1")) break;

                if (fullName.isBlank()){
                    System.err.println("Passenger name is required. Please try again.");
                    continue;
                }

                System.out.println("Enter passenger age.");
                int age = input.nextInt();
                input.nextLine();

                if (age < 1) {
                    System.out.println("Passenger age must be greater than 0. Please try again.");
                    continue;
                }

                System.out.println("Enter seat number:");
                int seatNumber = input.nextInt();
                input.nextLine();

                Ticket ticket = selectedTrip.reserveSeatAndIssueTicket(seatNumber, fullName, age);

                if (ticket == null) {
                    System.err.println("Ticket could not be issued. Please check the seat number.");
                    continue;
                } else {
                    System.out.println("=== RESERVATION CREATED ===");
                    System.out.println(ticket);
                    System.out.println();
                }
                selectedTrip.printSeatAvailability();
            }

        } while (true);
        System.out.println("Have a good trip!");
    }
}