package dat.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;
    private int userId;
    private int destinationId;
    private int rating;
    private String comment;

    public Review(int id, int userId, int destinationId, int rating, String comment) {
        this.id = id;
        this.userId = userId;
        this.destinationId = destinationId;
        this.rating = rating;
        this.comment = comment;
    }
}
