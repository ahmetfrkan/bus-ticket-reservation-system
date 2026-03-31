package classes;

import enums.NumberingStyle;
import enums.SeatFormat;
import enums.SeatPosition;

import java.util.ArrayList;
import java.util.List;

public class Bus {
    private final SeatFormat seatFormat; // Is it 2+1 or 2+2?
    private final int totalSeats; // How many seats are there?
    private final boolean hasBackRow; // Are there bench-type seats in the back? Checked in 2+1
    private final NumberingStyle numberingStyle; // Will be checked in 2+1 buses.

    private static final int DOOR_ROW_INDEX = 7;

    // Seats will be dynamically created according to the information given above and added to this list.
    private final List<Seat> seats = new ArrayList<>();

    public Bus(SeatFormat seatFormat, int totalSeats, boolean hasBackRow, NumberingStyle numberingStyle) {
        this.seatFormat = seatFormat;
        this.totalSeats = totalSeats;
        this.hasBackRow = hasBackRow;
        this.numberingStyle = numberingStyle;

        // Very simple input check
        if (totalSeats < 4){
            throw new IllegalArgumentException("Seat count must be at least 4.");
        }

        generateSeats();
    }

    // Seats will be created dynamically. The creation process will be done in two stages.
    // The rearmost seats of the bus will be considered as one part, and the rest of the seats
    // will be considered as another part.
    // Meaning, the rearmost will be backRow seats, the rest will be body seats.
    private void generateSeats() {

        // Based on the hasBackRow boolean value, how many seats will be in the back of 2+1 buses?
        int backRowSeatCount = getBackRowSeatCount();
        int bodySeatCount = this.totalSeats - backRowSeatCount; // In a 41-seat 2+1 bus, if there are 4 seats in the back, there are 37 seats in the body.

        if (this.seatFormat == SeatFormat.TWO_PLUS_ONE) {
            // Generate based on 2+1
            generateTwoPlusOneSeats(bodySeatCount, backRowSeatCount);
        } else {
            // Generate based on 2+2
            generateTwoPlusTwoSeats(bodySeatCount, backRowSeatCount);
        }
    }

    // Helper 1
    private int getBackRowSeatCount() {
        if (this.seatFormat == SeatFormat.TWO_PLUS_ONE){
            return this.hasBackRow ? 4 : 0;
        }else {
            // Bus is 2+2
            return 5;
        }
    }

    private void generateTwoPlusOneSeats(int bodySeatCount, int backRowSeatCount) {
        int produced = 0;
        int rowIndex = 0;

        if (this.numberingStyle == NumberingStyle.CONTINUOUS){
            // Continuous seat numbers have the same logic as 2+2.
            int currentSeatNumber = 1;
            while (produced < bodySeatCount) {
                // Is the right side available for a seat?
                boolean rightSideAvailable = (rowIndex != DOOR_ROW_INDEX);

                // Left window side
                if (produced < bodySeatCount) {
                    addSeat(currentSeatNumber, rowIndex, SeatPosition.LEFT);
                    currentSeatNumber++;
                    produced++;
                } else break;

                // The only difference from the 2+2 logic is that the left aisle seat is not created.

                // Right side
                if (rightSideAvailable) {
                    // Right aisle side
                    if (produced < bodySeatCount) {
                        addSeat(currentSeatNumber, rowIndex, SeatPosition.RIGHT_INNER);
                        currentSeatNumber++;
                        produced++;
                    } else break;

                    // Right window side
                    if (produced < bodySeatCount) {
                        addSeat(currentSeatNumber, rowIndex, SeatPosition.RIGHT);
                        currentSeatNumber++;
                        produced++;
                    } else break;
                }
                rowIndex++;
            }

            // Back seats, if any
            // If hasBackRow is false, backRowSeatCount will be 0, so this loop will not run. This way,
            // the 2+1 creation logic above is applied to the whole bus.
            generateBackRowSeats(backRowSeatCount, currentSeatNumber);
        } else{
            // Skipping
            // 1   3  4
            // 5   7  8
            // 9  11 12

            int currentSeatNumber = 1;

            while (produced < bodySeatCount) {
                boolean rightSideAvailable = (rowIndex != DOOR_ROW_INDEX);

                // Left window side - number 1
                if (produced < bodySeatCount){
                    addSeat(currentSeatNumber, rowIndex, SeatPosition.LEFT);
                    produced++;
                }else break;

                if (rightSideAvailable){
                    // Right aisle side - number 3
                    if (produced < bodySeatCount){
                        addSeat(currentSeatNumber + 2, rowIndex, SeatPosition.RIGHT_INNER);
                        produced++;
                    }else break;
                    // Right window side - number 4
                    if (produced < bodySeatCount){
                        addSeat(currentSeatNumber + 3, rowIndex, SeatPosition.RIGHT);
                        produced++;
                    }else break;
                    currentSeatNumber += 4;
                }else {
                    // If there is a door
                    currentSeatNumber += 2;

                }
                rowIndex++;
            }
            generateBackRowSeats(backRowSeatCount, currentSeatNumber);
        }
    }

    private void generateTwoPlusTwoSeats(int bodySeatCount, int backRowSeatCount) {
        int currentSeatNumber = 1; // Seat number will increase by 1 after each seat.
        int produced = 0; // How many seats were created (produced < bodySeatCount)
        int rowIndex = 0; // The row of seats starting from the front

        // Loop where body seats will be created
        while (produced < bodySeatCount) {
            // Is the right side available for a seat?
            boolean rightSideAvailable = (rowIndex != DOOR_ROW_INDEX);

            // Left window side
            if (produced < bodySeatCount) {
                addSeat(currentSeatNumber, rowIndex, SeatPosition.LEFT);
                currentSeatNumber++;
                produced++;
            } else break;

            // Left aisle side
            if (produced < bodySeatCount) {
                addSeat(currentSeatNumber, rowIndex, SeatPosition.LEFT_INNER);
                currentSeatNumber++;
                produced++;
            } else break;

            if (rightSideAvailable){
                // Right aisle side
                if (produced < bodySeatCount) {
                    addSeat(currentSeatNumber, rowIndex, SeatPosition.RIGHT_INNER);
                    currentSeatNumber++;
                    produced++;
                } else break;

                // Right window side
                if (produced < bodySeatCount) {
                    addSeat(currentSeatNumber, rowIndex, SeatPosition.RIGHT);
                    currentSeatNumber++;
                    produced++;
                } else break;
            }
            rowIndex++;
        }

        // Back seats
        generateBackRowSeats(backRowSeatCount, currentSeatNumber);
    }

    // Helper 2
    private void addSeat(int seatNumber, int rowIndex, SeatPosition seatPosition) {
        this.seats.add(new Seat(seatNumber, rowIndex, seatPosition));
    }

    // Helper 3
    private void generateBackRowSeats(int backRowSeatCount, int currentSeatNumber) {
        for (int i = 0; i < backRowSeatCount; i++){
            addSeat(currentSeatNumber, -1, SeatPosition.BACK_ROW);
            currentSeatNumber++;
        }
    }

    // Helper 4
    private Seat findSeat(int rowIndex, SeatPosition position){
        for (Seat s : this.seats){
            if (s.getRowIndex() == rowIndex && s.getSeatPosition() == position){
                return s;
            }
        }
        return null; // No seat found at the specified row and position!!!
    }

    // Print seats to match the visual layout of a bus
    public void printSeats(){

        System.out.println("==== Bus Seat Layout ====");

        int maxRowIndex = 0;

        for (Seat seat : this.seats) {
            int rowIndex = seat.getRowIndex();
            if (rowIndex > maxRowIndex) {
                maxRowIndex = rowIndex;
            }
        }

        // Body print
        for (int row = 0; row <= maxRowIndex; row++){
            if (this.seatFormat == SeatFormat.TWO_PLUS_ONE){
                Seat left = findSeat(row, SeatPosition.LEFT);

                Seat rightInner = findSeat(row, SeatPosition.RIGHT_INNER);
                Seat right = findSeat(row, SeatPosition.RIGHT);

                // PRINT LEFT SEAT TO CONSOLE
                if (left != null) {
                    System.out.printf("%2d", left.getSeatNumber());
                }else{
                    System.out.print("  ");
                }

                // AISLE
                System.out.print("    ");

                // RIGHT SIDE SEATS

                if (row == DOOR_ROW_INDEX){
                    System.out.print(" DOOR");
                }

                // Right aisle side
                if (rightInner != null) {
                    System.out.printf("%2d ", rightInner.getSeatNumber());
                }else if(row != DOOR_ROW_INDEX) {
                    System.out.print("  ");
                }
                // Right window side
                if (right != null) {
                    System.out.printf("%2d", right.getSeatNumber());
                }else if(row != DOOR_ROW_INDEX){
                    System.out.print("  ");
                }

                System.out.println();
            } else { // 2+2
                Seat left = findSeat(row, SeatPosition.LEFT);
                Seat leftInner = findSeat(row, SeatPosition.LEFT_INNER);
                Seat rightInner = findSeat(row, SeatPosition.RIGHT_INNER);
                Seat right = findSeat(row, SeatPosition.RIGHT);

                // PRINT LEFT WINDOW SIDE SEAT TO CONSOLE
                if (left != null) {
                    System.out.printf("%2d ", left.getSeatNumber());
                }else{
                    System.out.print(" ");
                }
                // PRINT LEFT AISLE SIDE SEAT TO CONSOLE
                if (leftInner != null) {
                    System.out.printf("%2d", leftInner.getSeatNumber());
                }else{
                    System.out.print(" ");
                }

                // AISLE
                System.out.print("    ");

                // RIGHT SIDE SEATS

                if (row == DOOR_ROW_INDEX){
                    System.out.print(" DOOR");
                }

                // Right aisle side
                if (rightInner != null) {
                    System.out.printf("%2d ", rightInner.getSeatNumber());
                }else if(!(row == DOOR_ROW_INDEX)) {
                    System.out.print("  ");
                }
                // Right window side
                if (right != null) {
                    System.out.printf("%2d", right.getSeatNumber());
                }else if(!(row == DOOR_ROW_INDEX)){
                    System.out.print("  ");
                }

                System.out.println();
            }
        }

        // Rearmost seats
        for (Seat s : this.seats){
            if (s.getRowIndex() == -1){
                System.out.printf("%2d ", s.getSeatNumber());
            }
        }
        System.out.println();
        System.out.println("================================");
    }

    public SeatFormat getSeatFormat() {
        return seatFormat;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public boolean isHasBackRow() {
        return hasBackRow;
    }

    public NumberingStyle getNumberingStyle() {
        return numberingStyle;
    }

    public List<Seat> getSeats() {
        return seats;
    }
}