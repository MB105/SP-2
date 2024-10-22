package dat.entities;

import dat.dtos.BookingDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Setter
    @Column(name = "booking_id", nullable = false)
    private int bookingId;

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

    public Booking(int bookingId, Destination destination, LocalDateTime departureDate, LocalDateTime arrivalDate, LocalDate bookingDate, BookingStatus status) {
        this.bookingId = bookingId;
        this.destination = destination;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    // Conversion constructor
    public Booking(BookingDTO bookingDTO, Destination destination) {
        this.id = bookingDTO.getId();
        this.bookingId = bookingDTO.getBookingId();
        this.destination = destination; // Use the destination passed from the DTO
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
        return id == booking.id && bookingId == booking.bookingId && Objects.equals(destination, booking.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookingId, destination);
    }
}
