package dat.dtos;

import dat.entities.Booking;
import dat.entities.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Integer id;
    private Integer destinationId;
    private String destinationCity;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private LocalDate bookingDate;
    private BookingStatus status;

    public BookingDTO(Booking booking) {
        this.destinationId = booking.getDestination() != null ? booking.getDestination().getId() : null;
        this.destinationCity = booking.getDestination() != null ? booking.getDestination().getCity() : null;
        this.departureDate = booking.getDepartureDate();
        this.arrivalDate = booking.getArrivalDate();
        this.bookingDate = booking.getBookingDate();
        this.status = booking.getStatus();
        this.id = booking.getId() != null ? booking.getId() : null;
    }

    @Override
    public boolean equals(Object o) {
        // sammenligner to BookingDTO objekter for lighed
        if (this == o) return true; // hvis de er det samme objekt
        if (!(o instanceof BookingDTO bookingDTO)) return false; // tjekker om objektet er en BookingDTO

        if (id != null) {
            return id.equals(bookingDTO.id); // sammenligner id hvis det ikke er null
        }

        // sammenligner alle relevante felter for lighed
        return id == bookingDTO.id &&
                Objects.equals(destinationCity, bookingDTO.destinationCity) &&
                Objects.equals(departureDate, bookingDTO.departureDate) &&
                Objects.equals(arrivalDate, bookingDTO.arrivalDate) &&
                Objects.equals(bookingDate, bookingDTO.bookingDate) &&
                Objects.equals(status, bookingDTO.status);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode(); // returnerer hashkode baseret på id hvis det ikke er null
        }

        // genererer hashkode baseret på de relevante felter
        return Objects.hash(id, destinationCity, departureDate, arrivalDate, bookingDate, status);
    }
}