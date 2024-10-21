package dat.dtos;

import dat.entities.BookingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingDTO {
    private int id;
    private int bookingId;
    private int destinationId;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private LocalDate bookingDate;
    private BookingStatus status; // Ændret til BookingStatus enum

    public BookingDTO(int id, int bookingId, int destinationId, LocalDateTime departureDate, LocalDateTime arrivalDate, LocalDate bookingDate, BookingStatus status) {
        this.id = id;
        this.bookingId = bookingId;
        this.destinationId = destinationId;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.bookingDate = bookingDate;
        this.status = status;
    }
}
