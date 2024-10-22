package dat.entities;

import dat.dtos.ReviewDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    @Setter
    @Column(name = "rating", nullable = false)
    private int rating;

    @Setter
    @Column(name = "comment", length = 500)
    private String comment;

    // Constructor for creating a new review
    public Review(Destination destination, int rating, String comment) {
        this.destination = destination;
        this.rating = rating;
        this.comment = comment;
    }

    // Conversion constructor from DTO
    public Review(ReviewDTO reviewDTO, Destination destination) {
        this.id = reviewDTO.getId();
        this.rating = reviewDTO.getRating();
        this.comment = reviewDTO.getComment();
        this.destination = destination; // Set the destination from the DTO context
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id == review.id && Objects.equals(destination, review.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, destination);
    }
}
