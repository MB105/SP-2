package dat.dtos;

import dat.entities.Booking;
import dat.entities.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Integer id; // Assuming Booking entity now has this as primary key
    private int bookingId;
    private int destinationId; // Assuming this is the ID of the destination booked
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private LocalDate bookingDate;
    private BookingStatus status;

    public BookingDTO(Booking booking) {
        this.id = booking.getId();
        this.bookingId = booking.getBookingId();
        this.destinationId = booking.getDestination() != null ? booking.getDestination().getId() : null; // Ensure destination is not null
        this.departureDate = booking.getDepartureDate();
        this.arrivalDate = booking.getArrivalDate();
        this.bookingDate = booking.getBookingDate();
        this.status = booking.getStatus();
    }

    public static List<BookingDTO> toBookingDTOList(List<Booking> bookings) {
        return bookings.stream().map(BookingDTO::new).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Same object
        if (!(o instanceof BookingDTO bookingDTO)) return false; // Different class

        // If the ID is set, compare IDs; otherwise, compare based on other fields
        if (id != null) {
            return id.equals(bookingDTO.id);
        }

        return bookingId == bookingDTO.bookingId &&
                destinationId == bookingDTO.destinationId &&
                Objects.equals(departureDate, bookingDTO.departureDate) &&
                Objects.equals(arrivalDate, bookingDTO.arrivalDate) &&
                Objects.equals(bookingDate, bookingDTO.bookingDate) &&
                Objects.equals(status, bookingDTO.status);
    }

    @Override
    public int hashCode() {
        // Use ID for hash code if set; otherwise, use other significant fields
        if (id != null) {
            return id.hashCode();
        }

        return Objects.hash(bookingId, destinationId, departureDate, arrivalDate, bookingDate, status);
    }
}
