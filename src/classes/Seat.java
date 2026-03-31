package classes;

import enums.SeatPosition;

public class Seat {
    private final int seatNumber; // Number written on the seat
    private final int rowIndex; // Front of the bus is index 0. Which row is the seat in?
    private final SeatPosition seatPosition; // Where is the seat located in the bus?

    public Seat(int seatNumber, int rowIndex, SeatPosition seatPosition) {
        this.seatNumber = seatNumber;
        this.rowIndex = rowIndex;
        this.seatPosition = seatPosition;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public SeatPosition getSeatPosition() {
        return seatPosition;
    }
}