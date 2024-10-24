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
    private Integer id;

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

    public Booking(BookingDTO bookingDTO, Destination destination) {
        this.destination = destination;
        this.departureDate = bookingDTO.getDepartureDate();
        this.arrivalDate = bookingDTO.getArrivalDate();
        this.bookingDate = bookingDTO.getBookingDate();
        this.status = bookingDTO.getStatus();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id == booking.id &&
                Objects.equals(destination, booking.destination) &&
                Objects.equals(departureDate, booking.departureDate) &&
                Objects.equals(arrivalDate, booking.arrivalDate) &&
                Objects.equals(bookingDate, booking.bookingDate) &&
                Objects.equals(status, booking.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, destination, departureDate, arrivalDate, bookingDate, status);
    }
}
