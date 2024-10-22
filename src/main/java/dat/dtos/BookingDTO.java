package dat.dtos;

import dat.entities.Booking;
import dat.entities.BookingStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
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
        if (this == o) return true;
        if (!(o instanceof BookingDTO bookingDTO)) return false;

        return getId().equals(bookingDTO.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
