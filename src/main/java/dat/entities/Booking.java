package dat.entities;

import dat.dtos.BookingDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    @Setter
    @Column(name = "departure_date", nullable = false)
    private LocalDateTime departureDate;

    @Setter
    @Column(name = "arrival_date", nullable = false)
    private LocalDateTime arrivalDate;

    @Setter
    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Enumerated(EnumType.STRING)
    @Setter
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    // Constructor that does not set ID when creating a new booking
    public Booking(Destination destination, LocalDateTime departureDate, LocalDateTime arrivalDate, LocalDate bookingDate, BookingStatus status) {
        this.destination = destination;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    // Conversion constructor should not set the ID for new bookings
    public Booking(BookingDTO bookingDTO, Destination destination) {
        this.destination = destination; // Use the destination passed from the DTO
        this.departureDate = bookingDTO.getDepartureDate();
        this.arrivalDate = bookingDTO.getArrivalDate();
        this.bookingDate = bookingDTO.getBookingDate();
        this.status = bookingDTO.getStatus();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Same object
        if (o == null || getClass() != o.getClass()) return false; // Different class or null

        Booking booking = (Booking) o;

        // Check if ID is set and equals, else compare based on other fields
        if (id != 0 && booking.id != 0) {
            return id == booking.id; // Compare IDs if they are both set
        }

        // If IDs are not set (for new bookings), compare other fields
        return id == booking.id &&
                Objects.equals(destination, booking.destination) &&
                Objects.equals(departureDate, booking.departureDate) &&
                Objects.equals(arrivalDate, booking.arrivalDate) &&
                Objects.equals(bookingDate, booking.bookingDate) &&
                Objects.equals(status, booking.status);
    }

    @Override
    public int hashCode() {
        // If the ID is set, use it for hash code calculation; otherwise, use other fields
        if (id != 0) {
            return Objects.hash(id);
        }

        return Objects.hash(id, destination, departureDate, arrivalDate, bookingDate, status);
    }
}


