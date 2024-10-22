package dat.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;
    private int bookingId;
    private int destinationId;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private LocalDate bookingDate;

    @Enumerated(EnumType.STRING) // Brug enum som string
    private BookingStatus status; // Ændret til BookingStatus enum

    public Booking(int id, int bookingId, int destinationId, LocalDateTime departureDate, LocalDateTime arrivalDate, LocalDate bookingDate, BookingStatus status) {
        this.id = id;
        this.bookingId = bookingId;
        this.destinationId = destinationId;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.bookingDate = bookingDate;
        this.status = status;
    }
}
